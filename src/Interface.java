import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * Class Interface, with the application frame
 * extends JFrame
 * Add graphical components and logical stuff
 * 
 * @author fernandez.laura
 *
 */

@SuppressWarnings("serial")
public class Interface extends JFrame {

	/**
	 * Form length, number of questions in the form
	 */
	int formLength;

	/**
	 * Count stimuli played, to keep track of the order
	 */
	int count;

	/**
	 *  JPanels in which to place different components
	 */
	//JPanel panelImage;
	JPanel panelTask;
	JPanel panelSprecher;
	ArrayList<JPanel> allPanelSliders;
	JPanel panelLow;  

	/**
	 * Name of the listener subject of the experiments 
	 * inserted through JOptionPane.dialog
	 */
	String nameListener; 

	/**
	 * Number, gender, and age of the listener, to be identified in the statistics analysis
	 * inserted through JOptionPane.dialog
	 */

	String genderListener;
	String languageListener;
	int ageListener;

	/**
	 * Label in which to show the title, task
	 */
	JLabel task; 

	/**
	 * Label in which to show the 10 personality questions of the BFI-10
	 */
	JLabel derSprecher;

	/**
	 * Label with the loudspeaker icon
	 */
	JLabel image;

	/** 
	 * Continuous sliders in which to rate each form item
	 */
	ArrayList<SuperSlider> allSliders;

	/**
	 * Rating values
	 */
	final int minRating = 0;
	final int maxRating = 100;

	/**
	 * Hashtables and Labels for the sliders 
	 */
	ArrayList<Hashtable<Integer,JLabel>> allHashTables;
	ArrayList<JLabel> allJLabelLefts;
	ArrayList<JLabel> allJLabelRights;

	/**
	 * Change Listeners for the sliders: store the new value into the corresponding int rating
	 */

	ArrayList<ChangeListener> allChangeListeners;

	/**
	 * Ratings to every question, given by manipulating the sliders
	 */
	int[] allRatings;

	/**
	 * Font for the labels
	 */
	Font myFont20;

	/**
	 * Button to start the test and to listen to the next voice
	 */
	JButton buttonNext;

	/**
	 * Button to listen again to the same stimulus
	 */
	JButton buttonAgain;

	/**
	 * ActionListener for the Next button (pick the slider value)
	 */
	ButtonListener buttonActionListener;

	/**
	 * ActionListener for the button to listen again
	 */
	ButtonAgainListener buttonAgainActionListener;

	/**
	 * Instance of the class AudioPlayer, necessary to play the corresponding file
	 */
	AudioPlayer audioPlayer;

	/**
	 *   ListSpeech contains the list of speech that the listener will hear: words, sentences, text
	 */
	ListSpeech listSpeech;


	/**
	 * Class constructor
	 * Initialized variables and add listeners to buttons and add table and other graphical components
	 * @param name - name of the listener, subject of the experiments
	 * @param nl - number of the listener (order)
	 * @param al - age of the listener
	 * @param gl - gender of the listener
	 */
	public Interface(String name, int al, String gl, String ms){

		/**
		// Main window, instead of full screen		
		//setSize(1450,850); 
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(0,0,screenSize.width, screenSize.height);
		setVisible(true);
		 */


		// For full screen:
		setUndecorated(true);
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		setVisible(true);

		//info about the listener
		nameListener=name;
		ageListener=al;
		genderListener=gl;
		languageListener=ms;

		// Form length, number of items
		formLength=34;

		// Count of stimuli played, init to 1
		count=1;

		// Font for the labels
		myFont20=new Font("Arial",Font.BOLD,20);

		// Label (title/task) above the questionnaire items
		task = new JLabel("Inwieweit treffen die folgenden Attribute auf den Sprecher zu?");
		task.setFont(myFont20);

		// Icon
		image = new JLabel();
		ImageIcon icon = new ImageIcon("files\\images\\Loudspeaker.png");
		image.setIcon(icon);
		image.setVisible(false); // only display while playing stimuli

		// Init ratings ArrayList
		allRatings = new int[formLength];

		// Define labels and arrange the sliders (as many as formLength)
		createSliders();

		// Button Next and its Actionlistener
		buttonNext = new JButton();
		buttonNext.setText("Start");
		buttonActionListener= new ButtonListener(this);
		buttonNext.addActionListener(buttonActionListener);

		// Button Again and its Actionlistener
		buttonAgain = new JButton();
		buttonAgain.setText("Hören");
		buttonAgainActionListener= new ButtonAgainListener(this);
		buttonAgain.addActionListener(buttonAgainActionListener);
		buttonAgain.setVisible(false);


		// Define the panels and add GUI components
		createGUI();

		//Run the experiments, playing speech files listed in listSpeech
		listSpeech= new ListSpeech(this);

		// Start writing the output file 
		writeCSVfisrtLine();

	}

	/**
	 * Assign labels to sliders and adds change listeners
	 */
	private void createSliders() {

		// Init labels - Speaker Characteristics
		allJLabelLefts=new ArrayList<JLabel>();
		allJLabelRights=new ArrayList<JLabel>();

		allJLabelLefts.add(new JLabel("sympathisch"));
		allJLabelRights.add(new JLabel("unsympathisch"));

		allJLabelLefts.add(new JLabel("unsicher"));
		allJLabelRights.add(new JLabel("sicher"));

		allJLabelLefts.add(new JLabel("unattraktiv"));
		allJLabelRights.add(new JLabel("attraktiv"));

		allJLabelLefts.add(new JLabel("verständnisvoll"));
		allJLabelRights.add(new JLabel("verständnislos"));

		allJLabelLefts.add(new JLabel("entschieden"));
		allJLabelRights.add(new JLabel("unentschieden"));

		allJLabelLefts.add(new JLabel("aufdringlich"));
		allJLabelRights.add(new JLabel("unaufdringlich"));

		allJLabelLefts.add(new JLabel("nah")); 
		allJLabelRights.add(new JLabel("distanziert")); 

		allJLabelLefts.add(new JLabel("interessiert"));
		allJLabelRights.add(new JLabel("gelangweilt"));

		allJLabelLefts.add(new JLabel("emotionslos"));
		allJLabelRights.add(new JLabel("emotional"));

		allJLabelLefts.add(new JLabel("genervt"));
		allJLabelRights.add(new JLabel("nicht genervt"));

		allJLabelLefts.add(new JLabel("passiv"));
		allJLabelRights.add(new JLabel("aktiv"));

		allJLabelLefts.add(new JLabel("unangenehm"));
		allJLabelRights.add(new JLabel("angenehm"));

		allJLabelLefts.add(new JLabel("charaktervoll"));
		allJLabelRights.add(new JLabel("charakterlos"));

		allJLabelLefts.add(new JLabel("reserviert")); 
		allJLabelRights.add(new JLabel("gesellig"));

		allJLabelLefts.add(new JLabel("nervös"));
		allJLabelRights.add(new JLabel("entspannt"));

		allJLabelLefts.add(new JLabel("distanziert"));
		allJLabelRights.add(new JLabel("mitfühlend"));

		allJLabelLefts.add(new JLabel("unterwürfig"));
		allJLabelRights.add(new JLabel("dominant"));

		allJLabelLefts.add(new JLabel("affektiert"));
		allJLabelRights.add(new JLabel("unaffektiert"));

		allJLabelLefts.add(new JLabel("gefühlskalt"));
		allJLabelRights.add(new JLabel("herzlich"));

		allJLabelLefts.add(new JLabel("jung"));
		allJLabelRights.add(new JLabel("alt"));

		allJLabelLefts.add(new JLabel("sachlich"));
		allJLabelRights.add(new JLabel("unsachlich"));

		allJLabelLefts.add(new JLabel("aufgeregt"));
		allJLabelRights.add(new JLabel("ruhig"));

		allJLabelLefts.add(new JLabel("kompetent"));
		allJLabelRights.add(new JLabel("inkompetent"));

		allJLabelLefts.add(new JLabel("schön"));
		allJLabelRights.add(new JLabel("hässlich"));

		allJLabelLefts.add(new JLabel("unfreundlich"));
		allJLabelRights.add(new JLabel("freundlich"));

		allJLabelLefts.add(new JLabel("weiblich"));
		allJLabelRights.add(new JLabel("männlich"));

		allJLabelLefts.add(new JLabel("provokativ")); 
		allJLabelRights.add(new JLabel("gehorsam"));

		allJLabelLefts.add(new JLabel("engagiert"));
		allJLabelRights.add(new JLabel("gleichgültig"));

		allJLabelLefts.add(new JLabel("langweilig"));
		allJLabelRights.add(new JLabel("interessant"));

		allJLabelLefts.add(new JLabel("folgsam")); 
		allJLabelRights.add(new JLabel("zynisch"));

		allJLabelLefts.add(new JLabel("unaufgesetzt"));
		allJLabelRights.add(new JLabel("aufgesetzt"));

		allJLabelLefts.add(new JLabel("dumm"));
		allJLabelRights.add(new JLabel("intelligent"));

		allJLabelLefts.add(new JLabel("erwachsen"));
		allJLabelRights.add(new JLabel("kindlich"));

		allJLabelLefts.add(new JLabel("frech")); 
		allJLabelRights.add(new JLabel("bescheiden"));

		// Init Change Listeners
		allChangeListeners = new ArrayList<ChangeListener>();

		// Init sliders and set labels and set changeListener
		allSliders=new ArrayList<SuperSlider>();
		for (int i=0; i<formLength; i++){

			final SuperSlider sl=new SuperSlider(i, minRating, maxRating, (maxRating-minRating)/2);

			JLabel le=allJLabelLefts.get(i);
			JLabel ri=allJLabelRights.get(i);
			if (i<formLength/2){
				le.setPreferredSize(new Dimension(100, 10));
				ri.setPreferredSize(new Dimension(100, 10));
			}else{
				le.setPreferredSize(new Dimension(80, 10));
				ri.setPreferredSize(new Dimension(80, 10));
			}
			Hashtable<Integer, JLabel> lt = new Hashtable<Integer, JLabel>();
			lt.put( new Integer(minRating), le);
			lt.put( new Integer(maxRating), ri);

			sl.setLabelTable(lt);
			sl.setPaintLabels(true);
			sl.knobEnabled=false;    							 
			sl.knobPainted=false; 
			sl.setPreferredSize(new Dimension(500, 50));

			ChangeListener cl = new ChangeListener()    
			{
				public void stateChanged(ChangeEvent event)
				{
					// enable to paint the knob!
					if (sl.knobEnabled==false){
						sl.knobEnabled=true;     					 
					}

					// update text field when the slider value changes
					SuperSlider source = (SuperSlider) event.getSource();

					// Log all slider value changes
					System.out.println("SliderNumber:"+source.getSliderNumber()+",  Value:"+source.getValue());  

					// Store current rating for this slider
					allRatings[source.getSliderNumber()]= source.getValue();
				}
			};
			sl.addChangeListener(cl);


			allSliders.add(sl);

		}

	}


	/**
	 * Create panels, place all GUI components, add panels to container
	 */
	private void createGUI() {
		// Panel with the title/task label	
		panelTask = new JPanel(); //new GridLayout(2,1));
		panelTask.add(task, BorderLayout.CENTER);


		//Panels in which to place the sliders
		panelSprecher = new JPanel();
		panelSprecher.add(new JLabel());
		panelSprecher.add(new JLabel());
		panelSprecher.add(new JLabel());
		panelSprecher.add(new JLabel());


		allPanelSliders = new ArrayList<JPanel>();

		for (int i=0; i<formLength/2; i++){
			JPanel ps=new JPanel();
			ps.add(new JLabel());
			ps.add(allSliders.get(i)); 
			ps.add(new JLabel("                     "));
			ps.add(allSliders.get(i+formLength/2)); 
			ps.add(new JLabel()); 

			allPanelSliders.add(ps);
		}


		//Panel in which to place the Next button
		panelLow = new JPanel(new FlowLayout());  
		panelLow.add(buttonAgain, BorderLayout.WEST);
		panelLow.add(image, BorderLayout.CENTER);
		panelLow.add(buttonNext, BorderLayout.EAST);


		//add panels to frame
		Container container= getContentPane();
		container.setLayout(new GridLayout((formLength/2 +3),1));

		container.add(new JPanel());
		container.add(panelTask); 

		for (int i=0; i<allPanelSliders.size(); i++){
			container.add(allPanelSliders.get(i)); 
		}

		container.add(panelLow); 

	}


	/**
	 * Writes first row of CSV file (first line: column names)
	 */
	private void writeCSVfisrtLine() {
		String delim= ","; // comma

		String str= "filename" + delim + "nameListener" + delim + "ageListener" + delim + "genderListener" + delim + "languageListener" + delim;

		// For the questionnaire items, just write the right adjective
		for (int i=0; i<allJLabelRights.size()-1; i++){
			str = str + allJLabelRights.get(i).getText() + delim;
		}
		str = str + allJLabelRights.get(allJLabelRights.size()-1).getText();

		try {

			Path fileOutput = Paths.get("files\\output\\output_GUI_SpeakerCharacteristics_"+ nameListener + ".csv");
			Files.write(fileOutput, str.getBytes(), StandardOpenOption.CREATE_NEW);

		}catch (IOException e) {
			e.printStackTrace();
		}

	}


}
