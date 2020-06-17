package gradesdatabase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GUI1 {

	private JFrame frame;
	protected JTable gradesTable;
	protected JScrollPane gradesScroll;
	protected JTextField textField;
	protected String query;
	private JLabel lblInsert;
	protected JTextField textField_1;
	private JTextField textField2;
	private JTextField textField2_1;
	private JTextField commentField;
	protected JButton btnNewButton_3g;
	protected JButton btnInsert;
	private JComboBox<String> filter;
	protected JComboBox<String> students;
	private JComboBox<String> types;
	private JComboBox<String> grades1;
	
	public static String check;
	private static String log;
	private static int defaultWidth = 400;
	private static int defaultHeight = 300;
	private static String[] grades12 = {"1","2","3","4","5"};
	private static String[] list2 = {"Activity","Test","Exam"};
	private static String list[];
	DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
	Date now = new Date();
	
	static Logger logger = Logger.getLogger(GUI1.class);

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PropertyConfigurator.configure("log4j.properties");
					logger.info("PROGRAM RUNNING...");
					GUI1 window = new GUI1();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GUI1() {
		logger.trace("Initializing GUI.");
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		logger.trace("GUI Initialized");
		logger.info("GUI Initialized");
		frame = new JFrame("Gradebook");
		frame.setAlwaysOnTop(true);
		logger.info("Trying to connect to database...");
		java.sql.Connection conn = DBManagement.Connect();
		
		
		JPanel login = new JPanel();
		JPanel studentPanel = new JPanel();
		JPanel teacherPanel = new JPanel();
		JPanel tables = new JPanel();
		JPanel gradeIN = new JPanel();
		
		//login panel
		
		login.setBackground(Color.LIGHT_GRAY);
		login.setPreferredSize(new Dimension(defaultWidth,defaultHeight));
		login.setLayout(null);
		
		JLabel loginLabel = new JLabel("Login");
		loginLabel.setBounds(80, 110, 80, 25);
		login.add(loginLabel);
		
		JTextField loginText = new JTextField(20);
		loginText.setBounds(150, 110, 165, 25);
		login.add(loginText);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(80, 140, 80, 25);
		login.add(passwordLabel);
		
		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(150, 140, 165, 25);
		login.add(passwordText);
		
		JLabel resultLabel = new JLabel("");
		resultLabel.setBounds(65, 216, 300, 25);
		login.add(resultLabel);
		
		JButton button1_1 = new JButton ("Login");
		button1_1.setBounds(153, 178, 80, 25);
		button1_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				log = loginText.getText();
				String pass = passwordText.getText();
				check = DBManagement.checkLogin(conn,log, pass);
				logger.info("Login button pressed. Attempt to login.");
				if(check.equals("bad")) {
					resultLabel.setText("Invalid login or password."); 
					logger.info("Login failed. Incorrect input data.");
				}
				else if(check.equals("student")) {
					resultLabel.setText("Login Successful!");
					logger.info("Successful login. Redirecting to Student Mode panel.");
					query = "SELECT Name FROM Students where login='"+log+"';";
					textField.setText(DBManagement.getValue(conn,query));
					query = "SELECT Surname FROM Students where login='"+log+"';";
					textField_1.setText(DBManagement.getValue(conn,query));
					frame.setContentPane(studentPanel);
					frame.pack();
				}
				else if(check.equals("teacher")) {
					resultLabel.setText("Login Successful!");
					logger.info("Successful login. Redirecting to Teacher Mode panel.");
					query = "SELECT Teacher FROM Courses where login='"+log+"';";
					textField2.setText(DBManagement.getValue(conn,query));
					query = "SELECT Name FROM Courses where login='"+log+"';";
					textField2_1.setText(DBManagement.getValue(conn,query));
					frame.setContentPane(teacherPanel);
					frame.pack();
				}
			}
		});
		login.add(button1_1);
		
		//student panel
			
			studentPanel.setBackground(Color.LIGHT_GRAY);
			studentPanel.setPreferredSize(new Dimension(300, 200));
			studentPanel.setLayout(null);
			
			JLabel mode = new JLabel("Student Mode");
			mode.setBounds(200, 175, 100, 20);
			studentPanel.add(mode);
			
			JLabel lblNewLabel_1 = new JLabel("Name");
			lblNewLabel_1.setBounds(20, 20, 60, 20);
			studentPanel.add(lblNewLabel_1);
			
			JLabel lblNewLabel_2 = new JLabel("Surname");
			lblNewLabel_2.setBounds(20, 60, 60, 20);
			studentPanel.add(lblNewLabel_2);
			
			textField = new JTextField();
			textField.setBounds(100, 20, 100, 25);
			textField.setHorizontalAlignment(SwingConstants.CENTER);
			textField.setEditable(false);
			studentPanel.add(textField);
			
			textField_1 = new JTextField();
			textField_1.setBounds(100, 60, 100, 25);
			textField_1.setHorizontalAlignment(SwingConstants.CENTER);
			textField_1.setEditable(false);
			studentPanel.add(textField_1);
			
			JButton btnNewButton = new JButton("GRADES");
			btnNewButton.setBounds(50, 100, 100, 25);
			btnNewButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.info("Button GRADES pressed. Redirecting to grades panel.");
					tables.remove(gradesScroll);
					String gradesQuery = "SELECT  Course,c.Teacher Teacher, Grade, Date, Type, Comment FROM Grades g inner join Students s on s.S_id=g.S_id and s.login='"+log+"' inner join Courses c on c.Name=g.Course ;";
					String [] names = {"Course","Teacher","Grade","Date","Type","Comment"};
					gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
					gradesScroll.setEnabled(false);
					gradesTable.setFillsViewportHeight(true);
					gradesScroll.setBounds(10, 50, 580,200);
					tables.add(gradesScroll);
					tables.remove(filter);
					filter = new JComboBox<String>(names);
					filter.setBounds(500, 15, 100, 25);
					filter.setBackground(Color.WHITE);
					filter.setForeground(Color.BLUE);
					filter.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.info("Filter option chosen. Setting appropriate filtration.");
							tables.remove(gradesScroll);
							if(names[filter.getSelectedIndex()].equals("Date")) {
								String gradesQuery = "SELECT Course, Teacher, Grade, Date, Type, Comment FROM (SELECT g.Course Course, c.Teacher Teacher, g.Grade Grade,g.Date Date,g.Type Type,g.Comment Comment,Substring(Date,4,2) month From Grades g inner join Students s on s.S_id=g.S_id and s.login='"+log+"' inner join Courses c on c.Name=g.Course ORDER BY month,Date,Course)tmp;";										
								gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
								gradesScroll.setEnabled(false);
								gradesTable.setFillsViewportHeight(true);
								gradesScroll.setBounds(10, 50, 580,200);
								tables.add(gradesScroll);
							}
							else {
								String gradesQuery = "SELECT g.Course,c.Teacher, g.Grade ,g.Date ,g.Type ,g.Comment From Grades g inner join Students s on s.S_id=g.S_id inner join Courses c on c.Name=g.Course where s.login='"+log+"' ORDER BY "+names[filter.getSelectedIndex()]+";";										
								gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
								gradesScroll.setEnabled(false);
								gradesTable.setFillsViewportHeight(true);
								gradesScroll.setBounds(10, 50, 580,200);
								tables.add(gradesScroll);
							}
						}
					});
					filter.setSelectedIndex(1);
					tables.add(filter);
					
					tables.remove(btnNewButton_3g);
					btnNewButton_3g = new JButton("Back");
					btnNewButton_3g.setBounds(250, 270, 100, 25);
					btnNewButton_3g.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.trace("Button Back pressed. Redirecting to teacher panel.");
							frame.setContentPane(studentPanel);
							frame.pack();
						}
					});
					tables.add(btnNewButton_3g);
					frame.setContentPane(tables);
					frame.pack();
				}
			});
			studentPanel.add(btnNewButton);
			
			JButton btnNewButton_1 = new JButton("COURSES");
			btnNewButton_1.setBounds(150, 100, 100, 25);
			btnNewButton_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.info("Button COURSES pressed. Redirecting to tables panel.");
					tables.remove(gradesScroll);
					String gradesQuery = "SELECT DISTINCT g.Course Course, c.Teacher Teacher FROM Grades g inner join Courses c on c.Name=g.Course inner join Students s on s.S_id=g.S_id and s.login='"+log+"';";
					String [] names = {"Course","Teacher"};
					gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
					gradesScroll.setEnabled(false);
					gradesTable.setFillsViewportHeight(true);
					gradesScroll.setBounds(10, 50, 580,200);
					tables.add(gradesScroll);
					tables.remove(filter);
					filter = new JComboBox<String>(names);
					filter.setBounds(500, 15, 100, 25);
					filter.setBackground(Color.WHITE);
					filter.setForeground(Color.BLUE);
					filter.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.info("Filter option chosen. Setting appropriate filtration.");
							tables.remove(gradesScroll);
							String gradesQuery = "SELECT DISTINCT g.Course Course, c.Teacher Teacher FROM Grades g inner join Courses c on c.Name=g.Course inner join Students s on s.S_id=g.S_id and s.login='"+log+"' ORDER BY "+names[filter.getSelectedIndex()]+";";										
							gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
							gradesScroll.setEnabled(false);
							gradesTable.setFillsViewportHeight(true);
							gradesScroll.setBounds(10, 50, 580,200);
							tables.add(gradesScroll);
						}
					});
					filter.setSelectedIndex(1);
					tables.add(filter);
					
					tables.remove(btnNewButton_3g);
					btnNewButton_3g = new JButton("Back");
					btnNewButton_3g.setBounds(250, 270, 100, 25);
					btnNewButton_3g.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.trace("Button Back pressed. Redirecting to teacher panel.");
							frame.setContentPane(studentPanel);
							frame.pack();
						}
					});
					tables.add(btnNewButton_3g);
					frame.setContentPane(tables);
					frame.pack();
				}
			});
			studentPanel.add(btnNewButton_1);
			
			JButton btnNewButton_3s = new JButton("Log out");
			btnNewButton_3s.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.info("Button Log out pressed. Redirecting to login panel.");
					frame.setContentPane(login);
					frame.pack();
				}
			});
			btnNewButton_3s.setBounds(100, 150, 100, 25);
			studentPanel.add(btnNewButton_3s);
		
		//teacher panel
		
			teacherPanel.setBackground(Color.LIGHT_GRAY);
			teacherPanel.setPreferredSize(new Dimension(300, 300));
			teacherPanel.setLayout(null);
			
			JLabel mode2 = new JLabel("Teacher Mode");
			mode2.setBounds(200, 275, 100, 20);
			teacherPanel.add(mode2);
			
			JLabel lblNewLabel_12 = new JLabel("Name");
			lblNewLabel_12.setBounds(20, 20, 60, 20);
			teacherPanel.add(lblNewLabel_12);
			
			JLabel lblNewLabel_22 = new JLabel("Course");
			lblNewLabel_22.setBounds(20, 60, 60, 20);
			teacherPanel.add(lblNewLabel_22);
						
			textField2 = new JTextField();
			textField2.setHorizontalAlignment(SwingConstants.CENTER);
			textField2.setEditable(false);
			textField2.setBounds(100, 20, 100, 25);
			teacherPanel.add(textField2);
			
			textField2_1 = new JTextField();
			textField2_1.setBounds(100, 60, 100, 25);
			textField2_1.setHorizontalAlignment(SwingConstants.CENTER);
			textField2_1.setEditable(false);
			teacherPanel.add(textField2_1);
			
			JButton btnNewButton2_1 = new JButton("STUDENTS");
			btnNewButton2_1.setBounds(100, 120, 100, 25);
			btnNewButton2_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					logger.info("Button STUDENTS pressed. Redirecting to tables panel.");
					tables.remove(gradesScroll);
					tables.remove(filter);
					String gradesQuery = "SELECT concat(s.Name, s.Surname) Student, Grade, Date, Type, Comment FROM Grades g inner join Students s on s.S_id=g.S_id inner join Courses c on c.login='"+log+"' and c.Name=g.Course;";
					String [] names = {"Student","Grade","Date","Type","Comment"};
					gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
					gradesScroll.setEnabled(false);
					gradesTable.setFillsViewportHeight(true);
					gradesScroll.setBounds(10, 50, 580,200);
					tables.add(gradesScroll);
					tables.remove(filter);
					filter = new JComboBox<String>(names);
					filter.setBounds(500, 15, 100, 25);
					filter.setBackground(Color.WHITE);
					filter.setForeground(Color.BLUE);
					filter.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.info("Filter option chosen. Setting appropriate filtration.");
							tables.remove(gradesScroll);
							if(names[filter.getSelectedIndex()].equals("Date")) {
								String gradesQuery = "SELECT Student, Grade, Date, Type, Comment FROM (SELECT concat(s.Name,' ', s.Surname) Student,g.Grade Grade,g.Date Date,g.Type Type,g.Comment Comment,Substring(Date,4,2) month From Grades g inner join Students s on s.S_id=g.S_id inner join Courses c on c.Name=g.Course where c.login='"+log+"' ORDER BY month,Date,Student)tmp;";										
								gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
								gradesScroll.setEnabled(false);
								gradesTable.setFillsViewportHeight(true);
								gradesScroll.setBounds(10, 50, 580,200);
								tables.add(gradesScroll);
							}
							else {
								String gradesQuery = "SELECT concat(s.Name,' ', s.Surname) Student,g.Grade Grade,g.Date Date,g.Type Type,g.Comment Comment From Grades g inner join Students s on s.S_id=g.S_id inner join Courses c on c.Name=g.Course where c.login='"+log+"' ORDER BY "+names[filter.getSelectedIndex()]+";";										
								gradesScroll = new JScrollPane(DBManagement.getData(conn, names, gradesQuery));
								gradesScroll.setEnabled(false);
								gradesTable.setFillsViewportHeight(true);
								gradesScroll.setBounds(10, 50, 580,200);
								tables.add(gradesScroll);
							}
						}
					});
					filter.setSelectedIndex(1);
					tables.add(filter);
					
					tables.remove(btnNewButton_3g);
					btnNewButton_3g = new JButton("Back");
					btnNewButton_3g.setBounds(250, 270, 100, 25);
					btnNewButton_3g.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.trace("Button Back pressed. Redirecting to teacher panel.");
							frame.setContentPane(teacherPanel);
							frame.pack();
						}
					});
					tables.add(btnNewButton_3g);
					frame.setContentPane(tables);
					frame.pack();
				}
			});
			teacherPanel.add(btnNewButton2_1);
			
			JButton btnNewButton2_2 = new JButton("INSERT GRADE");
			btnNewButton2_2.setBounds(75, 170, 150, 50);
			btnNewButton2_2.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.info("Button INSERT GRADE pressed. Redirecting gradeIN panel.");
					gradeIN.remove(students);
					String query = new String("SELECT DISTINCT CONCAT(s.Name,' ',s.Surname) Student FROM Students s inner join Grades g on g.S_id=s.S_id inner join Courses c on c.Name = g.Course and c.login='"+log+"' ORDER BY Student;");
					list = DBManagement.getList(conn, query);
					students = new JComboBox <String>(list);
					students.setBounds(20, 50, 150, 25);
					students.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							logger.info("Option from list chosen.");
						}
					});
					gradeIN.add(students);
					frame.setContentPane(gradeIN);
					frame.pack();
				}
			});
			teacherPanel.add(btnNewButton2_2);
			
			JButton btnNewButton_32 = new JButton("Log out");
			btnNewButton_32.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					logger.info("Button Log out pressed. Redirecting to login panel.");
					frame.setContentPane(login);
					frame.pack();
				}
			});
			btnNewButton_32.setBounds(100, 250, 100, 25);
			teacherPanel.add(btnNewButton_32);
				
		//tables panel
		
		tables.setPreferredSize(new Dimension(600,300));
		tables.setBackground(Color.magenta);
		tables.setLayout(null);
		
		gradesTable = new JTable();
		gradesScroll = new JScrollPane(gradesTable);
		gradesScroll.setEnabled(false);
		gradesTable.setFillsViewportHeight(true);
		gradesScroll.setBounds(10, 50, 580,200);

		tables.add(gradesScroll);
		
		filter = new JComboBox<String>();
		filter.setBounds(500, 15, 100, 25);
		filter.setBackground(Color.WHITE);
		filter.setForeground(Color.BLUE);
		
		btnNewButton_3g = new JButton("Back");
		btnNewButton_3g.setBounds(250, 270, 100, 25);
		tables.add(btnNewButton_3g);
		
		JLabel filterLabel = new JLabel("Filter by:");
		filterLabel.setBounds(450, 15, 50, 25);
		tables.add(filterLabel);
		
		//grade insert panel
		
		gradeIN.setPreferredSize(new Dimension(450,200));
		gradeIN.setBackground(Color.CYAN);
		gradeIN.setLayout(null);
		
		JButton btnBack = new JButton("Back");
		btnBack.setBounds(10, 160, 100, 25);
		btnBack.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.trace("Button Back pressed. Redirecting to teacher panel.");
				frame.setContentPane(teacherPanel);
				frame.pack();
			}
		});
		gradeIN.add(btnBack);
		
		students = new JComboBox <String>();
		students.setBounds(20, 50, 150, 25);
		gradeIN.add(students);
		
		JLabel lblStudent = new JLabel("Student");
		lblStudent.setBounds(20, 20, 50, 25);
		gradeIN.add(lblStudent);
		
		JLabel lblType = new JLabel("Type");
		lblType.setBounds(200, 20, 50, 25);
		gradeIN.add(lblType);
		
		
		types = new JComboBox<String>(list2);
		types.setBounds(200, 50, 50, 25);
		gradeIN.add(types);
		
		
		grades1 = new JComboBox<String>(grades12);
		grades1.setBounds(270, 50, 50, 25);
		gradeIN.add(grades1);
		
		JLabel lblComment = new JLabel("Comment");
		lblComment.setBounds(340, 20, 60, 25);
		gradeIN.add(lblComment);
		
		commentField = new JTextField();
		commentField.setBounds(340, 50, 100, 25);
		gradeIN.add(commentField);
		
		JLabel lblGrade = new JLabel("Grade");
		lblGrade.setBounds(275, 20, 50, 25);
		gradeIN.add(lblGrade);
		
		lblInsert = new JLabel();
		lblInsert.setBounds(300, 120, 100, 25);
		gradeIN.add(lblInsert);
		
		btnInsert = new JButton("INSERT");
		btnInsert.setBounds(340, 80, 100, 25);
		btnInsert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("Button INSERT GRADE pressed. Inserting a grade to database");
				List<String> info = new ArrayList<String>();
				info.add(list[students.getSelectedIndex()]);
				info.add(grades12[grades1.getSelectedIndex()]);
				info.add(list2[types.getSelectedIndex()]);
				DBManagement.postGrade(conn,log, info, commentField.getText(),df.format(now));
				lblInsert.setText("Grade posted!");
			}
		});
		gradeIN.add(btnInsert);
		
		
		frame.setContentPane(login);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		logger.info("Login panel opened.");
	}
}