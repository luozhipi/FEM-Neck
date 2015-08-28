package artisynth.models.necksim;

import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import artisynth.core.gui.ControlPanel;
import artisynth.core.gui.widgets.DoubleFieldSlider;

public class HNControlPanel {
   public static void createHNeckPanel(NeckSimModel headneck, ControlPanel panel)
   {
      if (panel == null)
         return;
      addHeadRotationControls(headneck, panel);
      addJawMusclesControls(headneck, panel);
      //addSkinDynamicControls(headneck, panel);
      panel.setScrollable (true);
   }
   public static void addSkinDynamicControls(NeckSimModel headneck, ControlPanel panel)
   {
      if (panel == null)
         return;
      JLabel label = (JLabel)panel.addWidget (new JLabel("skin dynamicsProps"));
      label.setForeground (Color.black);
      panel.addWidget(new JSeparator());
      DoubleFieldSlider slider =
      (DoubleFieldSlider)panel.addWidget("particle mass", headneck, 
         "particleMass",0.0, 100);
      slider.getLabel ().setForeground (Color.black);
      slider =
      (DoubleFieldSlider)panel.addWidget("particle damping", headneck, 
         "particleDamping",0.0, 100);
      slider.getLabel ().setForeground (Color.black);
      slider =
      (DoubleFieldSlider)panel.addWidget("spring stiff", headneck, 
         "springStiff",0.0, 1000000);
      slider.getLabel ().setForeground (Color.black);
      slider =
      (DoubleFieldSlider)panel.addWidget("spring damping", headneck, 
         "springDamping",0.0, 10000);
      slider.getLabel ().setForeground (Color.black);
      
      slider =
      (DoubleFieldSlider)panel.addWidget("deep spring stiff", headneck, 
         "springStiff2",0.0, 1000000);
      slider.getLabel ().setForeground (Color.black);
      slider =
      (DoubleFieldSlider)panel.addWidget("deep spring damping", headneck, 
         "springDamping2",0.0, 10000);
      slider.getLabel ().setForeground (Color.black);
   }
   public static void addHeadRotationControls(NeckSimModel headneck,
      ControlPanel panel)
   {
      if (panel == null)
         return;
      JLabel label = (JLabel)panel.addWidget (new JLabel("head rotation"));
      label.setForeground (Color.pink);
      panel.addWidget(new JSeparator());
      DoubleFieldSlider slider =
      (DoubleFieldSlider)panel.addWidget("Head Roll Base", headneck, "headRollBase", -30, 30);
      slider.setPaintTicks (true);
      slider.getLabel ().setForeground (Color.pink);
      slider =
      (DoubleFieldSlider)panel.addWidget("Head Roll Skull", headneck, "headRollSkull", -30, 30);
      slider.setPaintTicks (true);
      slider.getLabel ().setForeground (Color.pink);
      slider =
      (DoubleFieldSlider)panel.addWidget("Head Yaw Base", headneck, "headYawBase",  -30, 30);
      slider.setPaintTicks (true);
      slider.getLabel ().setForeground (Color.pink);
      slider =
      (DoubleFieldSlider)panel.addWidget("Head Yaw Skull", headneck, "headYawSkull",  -30, 30);
      slider.setPaintTicks (true);
      slider.getLabel ().setForeground (Color.pink);
      slider =
      (DoubleFieldSlider)panel.addWidget("Head Pitch", headneck, "headPitch",  -30, 30);
      slider.setPaintTicks (true);
      slider.getLabel ().setForeground (Color.pink);
   }
   public static void addJawMusclesControls(NeckSimModel headneck, ControlPanel panel)
   {
      /*JLabel label = (JLabel)panel.addWidget (new JLabel("jaw muscles"));
      label.setForeground (Color.red);
      panel.addWidget(new JSeparator());
      String[] muscleNames = new String[]{
                                          "jawOpen",
      };
      String[] controlNames = new String[]{
                                          "bilateral openers",
      };
      for (int i = 0; i < muscleNames.length; i++)
      {
         DoubleFieldSlider slider =
         (DoubleFieldSlider)panel.addWidget(controlNames[i], headneck, 
            muscleNames[i],
            0.0, 1.0);
         slider.getLabel ().setForeground (Color.red);
      }*/
      JLabel label = (JLabel)panel.addWidget (new JLabel("jaw movements"));
      label.setForeground (Color.red);
      panel.addWidget(new JSeparator());
         DoubleFieldSlider slider =
         (DoubleFieldSlider)panel.addWidget("jaw movements", headneck, 
            "jawOpen",
            -15, 15);
         slider.getLabel ().setForeground (Color.red);
   }
}
