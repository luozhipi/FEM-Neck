package artisynth.models.necksim;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;

import maspack.interpolation.Interpolation.Order;
import maspack.properties.PropertyList;
import maspack.render.GLViewer;
import maspack.render.GLViewer.AxialView;
import artisynth.core.driver.DriverInterface;
import artisynth.core.driver.Main;
import artisynth.core.gui.ControlPanel;
import artisynth.core.mechmodels.ExcitationComponent;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.MuscleExciter;
import artisynth.core.mechmodels.SphericalRpyJoint;
import artisynth.core.mechmodels.MechSystemSolver.Integrator;
import artisynth.core.modelbase.RootModel;
import artisynth.core.probes.NumericInputProbe;
import artisynth.models.necksim.InputData;

public class NeckSimModel extends RootModel{
   
   public MechModel myMechModel;
   public NeckSkin skin;
   public SkinBounder skinBounder;
   
   public NeckSimModel() throws IOException
   {
      super();
   }
   public NeckSimModel(String name) throws IOException, Exception
   {
      super(name);
      myMechModel = new MechModel("necksim");
      myMechModel.setIntegrator(Integrator.Trapezoidal);
      myMechModel.setMaxStepSizeSec(0.001);
      addModel (myMechModel);
      myMechModel.setGravity(0, -0, 0);
      NeckSkeleton ns = new NeckSkeleton(myMechModel);
      FEMCoupler femCoupler = new FEMCoupler(myMechModel);
      femCoupler.loadFEM ();
      TetFEMCoupler tetfemCoupler = new TetFEMCoupler(myMechModel);
      tetfemCoupler.loadFEM ();
      skin  = new NeckSkin("skin", myMechModel);
   }
   @Override
   public void attach(DriverInterface driver) {
      GLViewer viewer = getMainViewer ();
      viewer.setAxialView(AxialView.Top);
      viewer.setBackgroundColor (new Color(0.8f, 0.8f, 0.8f));
      skin.myViewer =viewer;
      viewer.addRenderable (skin);
      if (getControlPanels().size() == 0) { 
         createControlPanel (this, driver.getFrame());
      }
      try {
         double timestep = 0.01;//the smaller the faster
         double simulationtime = 20000;
         NumericInputProbe pitch =
         new NumericInputProbe (
            this, "headPitch", null); 
         pitch.setStopTime (simulationtime);
         pitch.loadEmpty();
         pitch.setActive (true);
         addInputProbe (pitch);
         pitch.addData(InputData.nertual, timestep);
         pitch.setInterpolationOrder(Order.Linear);  
         
         NumericInputProbe rollbase =
         new NumericInputProbe (
            this, "headRollBase", null); 
         rollbase.setStopTime (simulationtime);
         rollbase.loadEmpty();
         rollbase.setActive (true);
         addInputProbe (rollbase);
         rollbase.addData(InputData.nertual, timestep);
         rollbase.setInterpolationOrder(Order.Linear);  
         
         NumericInputProbe rollskull =
         new NumericInputProbe (
            this, "headRollSkull", null); 
         rollskull.setStopTime (simulationtime);
         rollskull.loadEmpty();
         rollskull.setActive (true);
         addInputProbe (rollskull);
         rollskull.addData(InputData.nertual, timestep);
         rollskull.setInterpolationOrder(Order.Linear);  
         
         NumericInputProbe yawbase =
         new NumericInputProbe (
            this, "headYawBase", null); 
         yawbase.setStopTime (simulationtime);
         yawbase.loadEmpty();
         yawbase.setActive (true);
         addInputProbe (yawbase);
         yawbase.addData(InputData.nertual, timestep);
         yawbase.setInterpolationOrder(Order.Linear);  
         
         NumericInputProbe yawskull =
         new NumericInputProbe (
            this, "headYawSkull", null); 
         yawskull.setStopTime (simulationtime);
         yawskull.loadEmpty();
         yawskull.setActive (true);
         addInputProbe (yawskull);
         yawskull.addData(InputData.nertual, timestep);
         yawskull.setInterpolationOrder(Order.Linear);  
         
         NumericInputProbe open =
         new NumericInputProbe (
            this, "jawOpen", null); 
          open.setStopTime (simulationtime);
          open.loadEmpty();
          open.setActive (true);
          addInputProbe (open);
          open.addData(InputData.jawmove, timestep);
          open.setInterpolationOrder(Order.Linear);  
      }
      catch (Exception e) {
         e.printStackTrace();
      }
   }
   public void createControlPanel(RootModel root, JFrame refFrame) {
      ControlPanel panel = new ControlPanel("HeadNeck Controls", "LiveUpdate");
      addHeadNeckOptions(panel);
      panel.pack();
      panel.setVisible(true);
      java.awt.Point loc = refFrame.getLocation();
      panel.setLocation(loc.x + refFrame.getWidth(), loc.y);
      panel.enableLiveUpdating(true);
      root.addControlPanel(panel);
   }
   public void addHeadNeckOptions(ControlPanel panel) {
      if (panel == null) return;
      HNControlPanel.createHNeckPanel(this, panel);
   }
   public static PropertyList myProps = new PropertyList(NeckSimModel.class,
      RootModel.class);
   static{
      myProps.add("jawOpen",
         "open jaw", 0.0);
      
      myProps.add("headRollBase",
        "degrees of head roll about base point", 0.0);
      myProps.add("headRollSkull",
         "degrees of head roll about skull point", 0.0);
     myProps.add("headYawBase",
        "degrees of head yaw about base point", 0.0);
     myProps.add("headYawSkull",
        "degrees of head yaw about skull point", 0.0);
     myProps.add("headPitch",
        "degrees of head pitch about base point", 0.0);
     
//     myProps.add("stiffthMem",
//        "stifness stiffthMem", GlobalParameters.thMemStiff);
//     myProps.add("stiffctrMem",
//        "degrees of head pitch about base point", GlobalParameters.ctrMemStiff);
//     myProps.add("stiffphc",
//        "degrees of head pitch about base point", GlobalParameters.defaultPharnyxStiffness);
   }
   public PropertyList getAllPropertyInfo() {
      return myProps;
   }
   public void setJawOpen(double e)
   {
      //ExcitationComponent c = (MuscleExciter)myMechModel.getMuscleExciters ().get ("bi_open");
      //c.setExcitation (e);
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-jaw");
      joint.setYaw (e);
      joint.setPitch (0);
      joint.setRoll (0);
   }
   public double getJawOpen()
   {
      //ExcitationComponent c = (MuscleExciter)myMechModel.getMuscleExciters ().get ("bi_open");
      //return c.getExcitation ();
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-jaw");
      return joint.getYaw ();
   }
   public void setHeadRollBase(double angle)//side
   {
      SphericalRpyJoint joint0 = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c7-base");
      joint0.setRoll (angle);
      //fixJoints();
   }
   public double getHeadRollBase()
   {
      SphericalRpyJoint joint0 = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c7-base");
      return joint0.getRoll ();
   }
   public void setHeadRollSkull(double angle)//side
   {
      SphericalRpyJoint joint0 = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      joint0.setRoll (angle);
      //fixJoints();
   }
   public double getHeadRollSkull()
   {
      SphericalRpyJoint joint0 = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      return joint0.getRoll ();
   }
   public void setHeadYawSkull(double angle)
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      joint.setYaw (angle);
   }
   public double getHeadYawSkull()
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      return joint.getYaw ();
   }
   public void setHeadYawBase(double angle)
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c7-base");
      joint.setYaw (angle);
   }
   public double getHeadYawBase()
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c7-base");
      return joint.getYaw ();
   }
   public void setHeadPitch(double angle)
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      joint.setPitch (angle);
   }
   public double getHeadPitch()
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      return joint.getPitch ();
   }
   public void fixJoints()
   {
      SphericalRpyJoint joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c6-c7");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
      joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c5-c6");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
      joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c4-c5");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
      joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c3-c4");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
      joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c2-c3");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
      joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("c1-c2");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
      joint = (SphericalRpyJoint)myMechModel.rigidBodyConnectors ().get ("skull-c1");
      joint.setRoll (0);
      joint.setYaw (0);
      joint.setPitch (0);
   }
}

