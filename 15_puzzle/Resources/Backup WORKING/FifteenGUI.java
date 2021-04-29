//777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
//                               			V.W.F Mattana 21128707
//777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777777
import java.util.*;
import javax.swing.*; 
import java.awt.*;
import java.awt.event.*;
import java.lang.Object;

//======================================================================================================
//                             					FIFTEEN PUZZLE GUI
//======================================================================================================
public class FifteenGUI extends JPanel implements ActionListener
{
	//******************************************************************************************************
	//											   DECLARATIONS
	//******************************************************************************************************
	JButton[] b = new JButton[16];
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                         DEFAULT CONSTRUCTOR
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public FifteenGUI()
	{
		for(int i=0;i<16;i++)
		{
			int number = i + 1;
			if(i!=15)
			{
				b[i] = new JButton(" "+number+" ");
			}
			else
				b[i] = new JButton(" ");
		}
		GridLayout grid = new GridLayout(4,4);
		setLayout(grid);
		for(int i=0;i<16;i++)
		{
			add(b[i]);
			b[i].addActionListener(this);
		}	
	}
	
	public void actionPerformed(ActionEvent e)
    {
	}

	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	//                                           	MAIN
	//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++	
	public static void main(String[] Args)
	{
		JFrame frame = new JFrame("15-Puzzle");             
		JComponent newContentPane = new FifteenGUI();
        frame.setContentPane(newContentPane);
        frame.setSize(400,400);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}