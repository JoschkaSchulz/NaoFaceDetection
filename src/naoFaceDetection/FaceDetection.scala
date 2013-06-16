/*
               ,'``.._   ,'``.
              :,--._:)\,:,._,.:       ALL GLORY TO
              :`--,''   :`...';\      THE HYPNOTOAD!
               `,'       `---'  `.
               /                 :
              /                   \
            ,'                     :\.___,-.
           `...,---'``````-..._    |:       \
             (                 )   ;:    )   \  _,-.
              `.              (   //          `'    \
               :               `.//  )      )     , ;
             ,-|`.            _,'/       )    ) ,' ,'
            (  :`.`-..____..=:.-':     .     _,' ,'
             `,'\ ``--....-)='    `._,  \  ,') _ '``._
          _.-/ _ `.       (_)      /     )' ; / \ \`-.'
         `--(   `-:`.     `' ___..'  _,-'   |/   `.)
             `-. `.`.``-----``--,  .'
               |/`.\`'        ,',');
                   `         (/  (/
 */

package naoFaceDetection

import javax.swing._
import java.awt.event.ActionListener
import java.awt.event.ActionEvent
import java.awt.Dimension
import com.typesafe.config.ConfigFactory

class FaceDetection extends JFrame("Face detection"){
	import JFrame._;
	
	setDefaultCloseOperation(EXIT_ON_CLOSE)
	setVisible(true)
	setSize(640, 480)
	
	val button = new JButton("Refresh")
	button.addActionListener(new RefreshActionListener())
	button.setPreferredSize(new Dimension(200,100))
	
	this.add(button)
}

class RefreshActionListener extends ActionListener with Application {
	def actionPerformed( e: ActionEvent) {
		println("Test")
	}
}

object FaceDetection extends Application {
  val win = new FaceDetection
}