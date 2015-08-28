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
import artisynth.core.mechmodels.MechSystemSolver.Integrator;
import artisynth.core.modelbase.RootModel;
import artisynth.models.necksim.AnimateSkin;

public class AnimateSkinModel extends RootModel{
   
   public MechModel myMechModel;
   public AnimateSkin skin;
   
   public AnimateSkinModel() throws IOException
   {
      super();
   }
   public AnimateSkinModel(String name) throws IOException, Exception
   {
      super(name);
      myMechModel = new MechModel("skinanimation");
      myMechModel.setIntegrator(Integrator.Trapezoidal);
      myMechModel.setMaxStepSizeSec(0.001);
      addModel (myMechModel);
      myMechModel.setGravity(0, -0, 0);
      skin  = new AnimateSkin("skin", myMechModel);
   }
   @Override
   public void attach(DriverInterface driver) {
      GLViewer viewer = getMainViewer ();
      viewer.setAxialView(AxialView.Top);
      viewer.setBackgroundColor (new Color(0.8f, 0.8f, 0.8f));
      skin.myViewer =viewer;
      viewer.addRenderable (skin);
   }
}

