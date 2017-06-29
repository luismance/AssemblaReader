package org.manaty.its;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.google.gson.Gson;

@SuppressWarnings("serial")
public class MainForm extends JFrame {

	private JPanel contentPane;
	private JTextField txtApiKey;
	private JLabel lblPassword;
	private final JPasswordField txtApiSecret = new JPasswordField();
	private List lstTickets;
	private JLabel lblTickets;
	private JLabel lblSpaces;
	private JLabel lblMessage;
	private List lstSpaces;
	private JTextArea txtTicketChanges;
	private JScrollPane scrollPane;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JLabel lblTicketChanges;
	private JCheckBox chckbxAllTickets;
	private JCheckBox chkBoxStatusChange;
	private JCheckBox chkBoxSummaryChange;
	private JLabel lblFilter;
	private JButton btnSaveAsCsv;
	private JLabel lblCurrentPage;
	private JLabel lblPageSize;
	private int itemsPerPage = 25;
	private int currentPage = 1;
	private int pageSize = 1;
	private Map<String, String> spacesMap = new HashMap<>();
	private Map<String, String> milestonesMap = new HashMap<>();
	private Map<String, String> customReportsMap = new HashMap<>();
	private JButton btnNext;
	private JLabel lblExport;
	private String rowSeparator = ";";

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm frame = new MainForm();
					frame.setResizable(false);
					frame.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainForm() {
		setTitle("Assembla Reader");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1292, 901);
		contentPane = new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel lblUsername = new JLabel("API-KEY");
		lblUsername.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblUsername.setBounds(10, 11, 65, 26);
		contentPane.add(lblUsername);

		txtApiKey = new JTextField();
		txtApiKey.setBounds(74, 11, 313, 26);
		contentPane.add(txtApiKey);
		txtApiKey.setColumns(10);

		lblPassword = new JLabel("API-SECRET");
		lblPassword.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPassword.setBounds(398, 11, 73, 26);
		contentPane.add(lblPassword);
		txtApiSecret.setBounds(481, 11, 339, 26);
		contentPane.add(txtApiSecret);

		JButton btnLogin = new JButton("AUTHENTICATE");
		btnLogin.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnLogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				try {

					String inputLine = getRequest("https://api.assembla.com/v1/spaces");
					Gson gson = new Gson();
					AssemblaSpace[] spaces = gson.fromJson(inputLine, AssemblaSpace[].class);
					// ===================================
					// ===========ADDING SPACES===========
					// ===================================
					if (spaces != null) {
						if (spaces.length > 0) {

							String fileName = "assembla-api-key";
							FileWriter fw = new FileWriter(fileName);
							fw.write(txtApiKey.getText() + ";" + txtApiSecret.getText());
							fw.flush();
							fw.close();

							lstSpaces.clear();
							spacesMap.clear();
							for (AssemblaSpace as : spaces) {
								lstSpaces.add(as.getName());
								spacesMap.put(as.getName(), as.getId());
							}
							lstSpaces.addItemListener(new ItemListener() {
								@Override
								public void itemStateChanged(ItemEvent e) {

									String spaceId = spacesMap.get(lstSpaces.getSelectedItem().toString());
									lblTickets.setText("TICKETS(Loading...)");
									populateMilestonesMap(spaceId);
									populateCustomReportsMap(spaceId);
									getTickets(spaceId);
									getPages(spaceId);
									lblTickets.setText("TICKETS");
								}
							});
						}
					}

				} catch (Exception error) {
					System.out.println("EXCEPTION : " + error.getMessage());
					error.printStackTrace();
				}
			}
		});

		btnLogin.setBounds(830, 13, 167, 23);
		contentPane.add(btnLogin);

		panel_1 = new JPanel();
		panel_1.setLayout(null);
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(10, 48, 205, 544);
		contentPane.add(panel_1);

		lblSpaces = new JLabel("SPACES");
		lblSpaces.setBounds(10, 11, 65, 14);
		panel_1.add(lblSpaces);
		lblSpaces.setFont(new Font("Tahoma", Font.BOLD, 11));

		lstSpaces = new List();
		lstSpaces.setBounds(10, 31, 185, 503);
		panel_1.add(lstSpaces);

		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(225, 48, 1041, 544);
		contentPane.add(panel);
		panel.setLayout(null);

		JDesktopPane desktopPane = new JDesktopPane();
		desktopPane.setBounds(302, 35, 1, 1);
		panel.add(desktopPane);

		lblTickets = new JLabel("TICKETS");
		lblTickets.setBounds(10, 11, 249, 14);
		panel.add(lblTickets);
		lblTickets.setFont(new Font("Tahoma", Font.BOLD, 11));

		lstTickets = new List();
		lstTickets.setBounds(10, 29, 1021, 474);
		lstTickets.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (lstTickets.getSelectedIndex() > 0) {
					String ticketItem = lstTickets.getSelectedItem().toString();
					getTicketChanges(spacesMap.get(lstSpaces.getSelectedItem()),
							ticketItem.substring(0, ticketItem.indexOf(rowSeparator)));
				}
			}
		});
		panel.add(lstTickets);

		JButton btnPrevios = new JButton("PREVIOUS");
		btnPrevios.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentPage > 1) {
					currentPage--;
					getTickets(spacesMap.get(lstSpaces.getSelectedItem()));
					lblCurrentPage.setText("" + currentPage);
				}
			}
		});
		btnPrevios.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnPrevios.setBounds(10, 509, 95, 23);
		panel.add(btnPrevios);

		btnNext = new JButton("NEXT");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (currentPage < pageSize) {
					currentPage++;
					getTickets(spacesMap.get(lstSpaces.getSelectedItem()));
					lblCurrentPage.setText("" + currentPage);
				}
			}
		});
		btnNext.setFont(new Font("Tahoma", Font.BOLD, 11));
		btnNext.setBounds(182, 509, 105, 23);
		panel.add(btnNext);

		lblCurrentPage = new JLabel("000");
		lblCurrentPage.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblCurrentPage.setBounds(115, 509, 27, 26);
		panel.add(lblCurrentPage);

		JLabel label = new JLabel("/");
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		label.setBounds(143, 509, 12, 26);
		panel.add(label);

		lblPageSize = new JLabel("000");
		lblPageSize.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblPageSize.setBounds(155, 509, 27, 26);
		panel.add(lblPageSize);

		btnSaveAsCsv = new JButton("EXPORT SLA AS CSV");
		btnSaveAsCsv.setBounds(858, 509, 173, 23);
		panel.add(btnSaveAsCsv);
		btnSaveAsCsv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportTicketsToCSV(spacesMap.get(lstSpaces.getSelectedItem()));
			}
		});
		// btnSaveAsCsv.setVisible(false);
		btnSaveAsCsv.setFont(new Font("Tahoma", Font.BOLD, 11));

		lblExport = new JLabel("");
		lblExport.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblExport.setBounds(647, 509, 201, 26);
		panel.add(lblExport);

		lblMessage = new JLabel(" ");
		lblMessage.setForeground(Color.RED);
		lblMessage.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblMessage.setBounds(297, 513, 551, 14);
		panel.add(lblMessage);

		panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(10, 603, 1256, 249);
		contentPane.add(panel_2);
		panel_2.setLayout(null);

		lblTicketChanges = new JLabel("TICKET CHANGES");
		lblTicketChanges.setBounds(10, 11, 185, 14);
		lblTicketChanges.setFont(new Font("Tahoma", Font.BOLD, 11));
		panel_2.add(lblTicketChanges);

		chckbxAllTickets = new JCheckBox("Display status change of all tickets");
		chckbxAllTickets.setBounds(215, 7, 205, 23);
		chckbxAllTickets.setVisible(false);
		panel_2.add(chckbxAllTickets);

		chkBoxStatusChange = new JCheckBox("Status Change");
		chkBoxStatusChange.setBounds(473, 7, 97, 23);
		panel_2.add(chkBoxStatusChange);
		chkBoxStatusChange.setVisible(false);
		chkBoxStatusChange.setSelected(true);

		chkBoxSummaryChange = new JCheckBox("Summary Change");
		chkBoxSummaryChange.setBounds(572, 7, 126, 23);
		chkBoxSummaryChange.setVisible(false);
		panel_2.add(chkBoxSummaryChange);

		lblFilter = new JLabel("FILTER :");
		lblFilter.setBounds(426, 11, 52, 14);
		lblFilter.setVisible(false);
		panel_2.add(lblFilter);
		lblFilter.setFont(new Font("Tahoma", Font.BOLD, 11));

		scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 36, 1236, 202);
		panel_2.add(scrollPane);

		txtTicketChanges = new JTextArea();
		scrollPane.setViewportView(txtTicketChanges);
		txtTicketChanges.setColumns(10);
		txtTicketChanges.setEditable(false);

		try {
			File f = new File("assembla-api-key");
			if (f.exists()) {
				BufferedReader br = new BufferedReader(new FileReader("assembla-api-key"));
				try {
					StringBuilder sb = new StringBuilder();
					String line = br.readLine();

					sb.append(line);
					String retrievedLine = sb.toString();
					String apiKey = retrievedLine.split(";")[0];
					String apiKeySecret = retrievedLine.split(";")[1];
					txtApiKey.setText(apiKey);
					txtApiSecret.setText(apiKeySecret);
				} finally {
					br.close();
				}
			}
		} catch (IOException e1) {

		}
	}

	private void exportTicketsToCSV(String spaceId) {
		try {
			String fileName = "ticket_report_SLAs_by_Milestone_" + lstSpaces.getSelectedItem().replace(' ', '-')
					+ ".csv";
			File f = new File(fileName);

			if (f.exists()) {
				int fileIncrementor = 1;
				do {
					fileName = "ticket_report_SLAs_by_Milestone_" + lstSpaces.getSelectedItem().replace(' ', '-') + "_"
							+ fileIncrementor + ".csv";
					f = new File(fileName);
					fileIncrementor++;
				} while (f.exists());
			}

			FileWriter fw = new FileWriter(fileName);

			for (String item : getTicketsForExport(spaceId)) {
				System.out.println("ITEM : " + item);
				fw.append(item + System.lineSeparator());
			}

			fw.flush();
			fw.close();

			JOptionPane.showMessageDialog(null, "Export done!");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void populateMilestonesMap(String spaceId) {
		milestonesMap.clear();
		Gson gson = new Gson();

		AssemblaMilestone[] assemblaMilestones;

		int i = 1;
		do {
			assemblaMilestones = gson.fromJson(
					getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/milestones/all?page=" + i),
					AssemblaMilestone[].class);
			if (assemblaMilestones != null && assemblaMilestones.length > 0) {
				for (AssemblaMilestone m : assemblaMilestones) {
					milestonesMap.put(m.getId(), m.getTitle());
				}
			}
			i++;
		} while (assemblaMilestones != null && assemblaMilestones.length > 0);

		// for (String m : milestonesMap.keySet()) {
		// System.out.println("Milestone: " + m + "," +
		// milestonesMap.get(m));
		// }
	}

	private void populateCustomReportsMap(String spaceId) {
		customReportsMap.clear();
		Gson gson = new Gson();

		AssemblaCustomReport[] customReports;
		AssemblaTeamReport teamReport;

		teamReport = gson.fromJson(
				getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets/custom_reports"),
				AssemblaTeamReport.class);
		if (teamReport != null) {
			customReports = teamReport.getTeam_reports();
			if (customReports != null && customReports.length > 0) {
				for (AssemblaCustomReport cr : customReports) {
					customReportsMap.put(cr.getTitle(), cr.getId());
				}
			}
		}

		// for (String m : milestonesMap.keySet()) {
		// System.out.println("Milestone: " + m + "," +
		// milestonesMap.get(m));
		// }
	}

	private long getDaysDifference(String spaceId, String ticketNumber) {

		Date endDate = new Date();
		Date startDate = new Date();
		Gson gson = new Gson();
		txtTicketChanges.setText("");
		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);
		DateTimeFormatter dtf = new DateTimeFormatterBuilder()
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toFormatter();
		Date dte;

		AssemblaTicket ticket = gson.fromJson(
				getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets/" + ticketNumber),
				AssemblaTicket.class);

		AssemblaTicketChange[] ticketChanges;
		int i = 1;
		do {
			ticketChanges = gson.fromJson(getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets/"
					+ ticketNumber + "/ticket_comments?per_page=100&page=" + i), AssemblaTicketChange[].class);
			if (ticketChanges != null) {
				if (ticketChanges.length > 0) {
					for (AssemblaTicketChange ticketChange : ticketChanges) {
						String ticketChangeContent = ticketChange.getTicket_changes();
						String[] statusChanges = ticketChangeContent.split("---");
						for (String statusChange : statusChanges) {
							String[] splitStatusChange = statusChange.split("- - ");
							for (String singleStatusChange : splitStatusChange) {
								if (singleStatusChange.contains("status")) {
									System.out.println("SINGLE STATUS CHANGE : " + singleStatusChange);
									String currentStatusTicketChange = singleStatusChange.replace("status", "");
									String oldValue = currentStatusTicketChange.split("  - ")[1].trim();
									String newValue = currentStatusTicketChange.split("  - ")[2].trim();
									dte = Date.from(LocalDate.parse(ticketChange.getCreated_on(), dtf)
											.atStartOfDay(ZoneId.systemDefault()).toInstant());

									if ((newValue.toLowerCase().contains("rejected")
											|| newValue.toLowerCase().contains("wont do now")
											|| newValue.toLowerCase().contains("invalid")
											|| newValue.toLowerCase().contains("fixed")
											|| newValue.toLowerCase().contains("test")
											|| newValue.toLowerCase().contains("review"))) {
										endDate = dte;

									}
								}
							}

						}

					}
				}
			}
			i++;
		} while (ticketChanges != null && ticketChanges.length > 0 && ticketChanges.length > 10);

		dte = Date.from(LocalDate.parse(ticket.getCreated_on(), dtf).atStartOfDay(ZoneId.systemDefault()).toInstant());
		startDate = dte;

		System.out.println("Start Date : " + startDate + ", End Date : " + endDate);

		Calendar sDate = Calendar.getInstance();
		sDate.setTime(startDate);

		Calendar eDate = Calendar.getInstance();
		eDate.setTime(endDate);

		long sDateDiffFromWeekend = Calendar.SATURDAY - sDate.get(Calendar.DAY_OF_WEEK);
		long eDateDiffFromWeekend = eDate.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY;
		System.out.println("sDateDiffFromWeekend : " + sDateDiffFromWeekend);
		System.out.println("eDateDiffFromWeekend : " + eDateDiffFromWeekend);

		long diff = getDateDiff(startDate, endDate, TimeUnit.DAYS);
		System.out.println("diff : " + diff);

		long startEndDiff = diff - (sDateDiffFromWeekend + eDateDiffFromWeekend);
		System.out.println("startEndDiff : " + startEndDiff);
		long weekCount = startEndDiff / 7;
		System.out.println("weekCount : " + weekCount);
		long removedWeekend = weekCount * 5;
		System.out.println("removedWeekend : " + removedWeekend);
		long result = removedWeekend + (sDateDiffFromWeekend + eDateDiffFromWeekend) - 1;
		if (startEndDiff < 0) {
			result = diff;
		}

		System.out.println("Output : " + result);

		return result;

	}

	private java.util.List<String> getTicketsForExport(String spaceId) {

		java.util.List<String> result = new ArrayList<>();
		Gson gson = new Gson();

		AssemblaTicket[] assemblaTickets;
		int i = 1;
		// SimpleDateFormat format = new
		// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH);

		DateTimeFormatter dtf = new DateTimeFormatterBuilder()
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toFormatter();

		String rowHeader = "Number;Summary;Milestone;Status;Created On;Completed Date;SLA;SLA (target);Worked Days";
		String reportId = "0";
		String customReportName = "All tickets (support)";
		if (!customReportsMap.containsKey(customReportName)) {
		} else {
			reportId = "u" + customReportsMap.get(customReportName);
		}

		result.add(rowHeader);
		do {
			lblExport.setText("Exporting page " + i + " of " + pageSize);

			assemblaTickets = gson.fromJson(
					getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets?report=" + reportId
							+ "&per_page=" + itemsPerPage + "&sort_by=ticket_number&sort_order=asc&page=" + i),
					AssemblaTicket[].class);

			if (assemblaTickets != null) {
				if (assemblaTickets.length > 0) {
					for (AssemblaTicket ticket : assemblaTickets) {
						Date sDate = Date.from(LocalDate.parse(ticket.getCreated_on(), dtf)
								.atStartOfDay(ZoneId.systemDefault()).toInstant());
						AssemblaCustomField customField = ticket.getCustom_fields();
						String SLA = (customField != null && customField.getSLA() != null
								&& customField.getSLA().length() > 0 ? customField.getSLA() : "No SLA");
						result.add(ticket.getNumber() + rowSeparator + ticket.getSummary() + rowSeparator
								+ (milestonesMap.get(ticket.getMilestone_id())) + rowSeparator + ticket.getStatus()
								+ rowSeparator + (DateFormat.getInstance().format(sDate)) + rowSeparator
								+ (ticket.getCompleted_date() != null
										? DateFormat.getInstance()
												.format(Date.from(LocalDate.parse(ticket.getCompleted_date(), dtf)
														.atStartOfDay(ZoneId.systemDefault()).toInstant()))
										: "Not Completed")
								+ rowSeparator + SLA + rowSeparator + getSLATarget(SLA) + rowSeparator
								+ getDaysDifference(spaceId, ticket.getNumber()));
					}
				}
			}
			i++;
		} while (assemblaTickets != null && assemblaTickets.length > 0);
		lblExport.setText("");
		return result;
	}

	private void getTickets(String spaceId) {
		Gson gson = new Gson();

		AssemblaTicket[] assemblaTickets;
		int i = 1;
		lstTickets.clear();

		DateTimeFormatter dtf = new DateTimeFormatterBuilder()
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toFormatter();

		String rowHeader = "Number;Summary;Milestone;Status;Created On;Completed Date;SLA;SLA (target);Worked days";
		String reportId = "0";
		String customReportName = "All tickets (support)";
		if (!customReportsMap.containsKey(customReportName)) {
			lblMessage.setText(lstSpaces.getSelectedItem() + " does not have Custom Report : " + customReportName);
		} else {
			reportId = "u" + customReportsMap.get(customReportName);
		}

		assemblaTickets = gson.fromJson(
				getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets?report=" + reportId
						+ "&per_page=" + itemsPerPage + "&sort_by=number&sort_order=desc&page=" + currentPage),
				AssemblaTicket[].class);

		if (assemblaTickets != null) {
			if (assemblaTickets.length > 0) {
				lstTickets.add(rowHeader);
				for (AssemblaTicket ticket : assemblaTickets) {
					// System.out.println("DATE TIME : " +
					// ticket.getCreated_on());
					Date sDate = Date.from(LocalDate.parse(ticket.getCreated_on(), dtf)
							.atStartOfDay(ZoneId.systemDefault()).toInstant());
					AssemblaCustomField customField = ticket.getCustom_fields();
					String SLA = (customField != null && customField.getSLA() != null
							&& customField.getSLA().length() > 0 ? customField.getSLA() : "No SLA");
					String lineToAdd = ticket.getNumber() + rowSeparator + ticket.getSummary() + rowSeparator
							+ (milestonesMap.get(ticket.getMilestone_id())) + rowSeparator + ticket.getStatus()
							+ rowSeparator + (DateFormat.getInstance().format(sDate)) + rowSeparator
							+ (ticket.getCompleted_date() != null
									? DateFormat.getInstance()
											.format(Date.from(LocalDate.parse(ticket.getCompleted_date(), dtf)
													.atStartOfDay(ZoneId.systemDefault()).toInstant()))
									: "Not Completed")
							+ rowSeparator + SLA + rowSeparator + getSLATarget(SLA) + rowSeparator
							+ getDaysDifference(spaceId, ticket.getNumber());
					lstTickets.add(lineToAdd);
				}
			}
		}

		lblCurrentPage.setText("" + currentPage);
		if (lstTickets.getItemCount() <= 0) {
			JOptionPane.showMessageDialog(null, "No Tickets");
		}
	}

	private String getSLATarget(String SLA) {
		switch (SLA.toLowerCase()) {
		case "blocker":
			return "2";
		case "critical":
			return "3";
		case "major":
			return "4";
		case "minor":
			return "";
		}
		return "";
	}

	private void getPages(String spaceId) {
		Gson gson = new Gson();

		AssemblaTicket[] assemblaTickets;
		int i = 1;
		pageSize = 0;
		String reportId = "0";
		String customReportName = "All tickets (support)";
		if (!customReportsMap.containsKey(customReportName)) {
		} else {
			reportId = "u" + customReportsMap.get(customReportName);
		}

		do {

			assemblaTickets = gson.fromJson(
					getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets?report=" + reportId
							+ "&per_page=" + itemsPerPage + "&sort_by=number&sort_order=desc&page=" + i),
					AssemblaTicket[].class);

			if (assemblaTickets != null) {
				if (assemblaTickets.length > 0) {
					pageSize++;
					lblPageSize.setText("" + pageSize);
				}
			}
			i++;
		} while (assemblaTickets != null && assemblaTickets.length > 0);
	}

	public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = date2.getTime() - date1.getTime();
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	private void getTicketChanges(String spaceId, String ticketNumber) {
		Gson gson = new Gson();
		txtTicketChanges.setText("");
		DateTimeFormatter dtf = new DateTimeFormatterBuilder()
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
				.appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")).toFormatter();
		DateFormat dateToString = new SimpleDateFormat("MMMM d, yyyy HH:mm", Locale.ENGLISH);
		Date dte;

		AssemblaTicket ticket = gson.fromJson(
				getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets/" + ticketNumber),
				AssemblaTicket.class);

		AssemblaTicketChange[] ticketChanges;
		int i = 1;
		do {
			ticketChanges = gson.fromJson(getRequest("https://api.assembla.com/v1/spaces/" + spaceId + "/tickets/"
					+ ticketNumber + "/ticket_comments?per_page=100&page=" + i), AssemblaTicketChange[].class);
			if (ticketChanges != null) {
				if (ticketChanges.length > 0) {
					for (AssemblaTicketChange ticketChange : ticketChanges) {
						String ticketChangeContent = ticketChange.getTicket_changes();
						String[] statusChanges = ticketChangeContent.split("---");
						for (String statusChange : statusChanges) {
							String[] splitStatusChange = statusChange.split("- - ");
							for (String singleStatusChange : splitStatusChange) {
								if (singleStatusChange.contains("status")) {
									// System.out.println("SINGLE STATUS CHANGE
									// : " + singleStatusChange);
									String currentStatusTicketChange = singleStatusChange.replace("status", "");
									String oldValue = currentStatusTicketChange.split("  - ")[1].trim();
									String newValue = currentStatusTicketChange.split("  - ")[2].trim();

									dte = Date.from(LocalDate.parse(ticketChange.getCreated_on(), dtf)
											.atStartOfDay(ZoneId.systemDefault()).toInstant());
									txtTicketChanges
											.setText(txtTicketChanges.getText()
													+ (txtTicketChanges.getText().length() > 0 ? System.lineSeparator()
															: "")
													+ dateToString.format(dte) + "," + oldValue + "," + newValue);

								}
							}
						}
					}
				}
			}
			i++;
		} while (ticketChanges != null && ticketChanges.length > 0);

		dte = Date.from(LocalDate.parse(ticket.getCreated_on(), dtf).atStartOfDay(ZoneId.systemDefault()).toInstant());
		txtTicketChanges.setText(
				txtTicketChanges.getText() + (txtTicketChanges.getText().length() > 0 ? System.lineSeparator() : "")
						+ dateToString.format(dte) + ",New");

		if (txtTicketChanges.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(null, "No Result");
		}

	}

	public String getRequest(String url) {

		String result = "";
		try {

			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");

			// add request header
			/*
			 * String authentication = txtApiKey.getText() + ":" +
			 * txtApiSecret.getText(); String encodedString =
			 * Base64.getEncoder().encodeToString(authentication.getBytes());
			 * System.out.println("ENCODED STRING : " + encodedString);
			 */
			// con.setRequestProperty("Authorization", "Basic " +
			// encodedString);
			con.setRequestProperty("X-Api-Key", txtApiKey.getText());
			con.setRequestProperty("X-Api-Secret", txtApiSecret.getText());

			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);

			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

			String currentLine = "";
			while ((currentLine = in.readLine()) != null) {
				if (result != "") {
					result += System.lineSeparator();
				}
				result += currentLine;
			}

			in.close();
		} catch (Exception error) {
			System.out.println(error.getMessage());
		}
		return result;
	}

	public static String padRight(String str, int num) {
		return String.format("%1$-" + num + "s", str);
	}

	public static String padLeft(String s, int n) {
		return String.format("%1$" + n + "s", s);
	}
}
