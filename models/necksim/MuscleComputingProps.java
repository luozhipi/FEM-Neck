package artisynth.models.necksim;

import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemMuscleModel;
import artisynth.core.femmodels.materials.GenericMuscle;
import artisynth.core.femmodels.materials.LinearMaterial;
import artisynth.core.femmodels.materials.MooneyRivlinMaterial;
import artisynth.core.femmodels.materials.NeoHookeanMaterial;
import artisynth.core.mechmodels.MechSystemSolver.Integrator;
import artisynth.core.util.TimeBase;

public class MuscleComputingProps{
   //1MPa=1000000牛顿/平方米
   //所以1MPa=10公斤/平方厘米(简称10公斤)
   //
   public static void setComputingProps(FemModel3d tissue)
   {
      tissue.setGravity (0, 0, 0);
      tissue.setDensity (100);
     // tissue.setMaterial (new MooneyRivlinMaterial(1037, 0, 0, 486, 0, 10000));
      //tissue.setMaterial (new NeoHookeanMaterial());
      //tissue.setPoissonsRatio (0.4);
      //tissue.setYoungsModulus (1200); //1pa=1N/㎡
      //tissue.setIncompressible (true);
      tissue.setMaterial(new LinearMaterial());
      tissue.setWarping(true);
      tissue.setElasticity(800000, 0.4);
//      tissue.setIncompressible (true);
      tissue.setParticleDamping (0.622);//6.22
      tissue.setStiffnessDamping (0.11);//0.11
      //tissue.setMaxStepSize (10 * TimeBase.MSEC);
      //tissue.setIntegrator (Integrator.ConstrainedBackwardEuler);
   }
   
   public static void setTetComputingProps(FemModel3d tissue)
   {
      tissue.setGravity (0, 0, 0);
      tissue.setDensity (100);
     // tissue.setMaterial (new MooneyRivlinMaterial(1037, 0, 0, 486, 0, 10000));
      //tissue.setMaterial (new NeoHookeanMaterial());
      //tissue.setPoissonsRatio (0.4);
      //tissue.setYoungsModulus (1200); //1pa=1N/㎡
      //tissue.setIncompressible (true);
      tissue.setMaterial(new LinearMaterial());
      tissue.setWarping(true);
      tissue.setElasticity(800000, 0.4);
//      tissue.setIncompressible (true);
      tissue.setParticleDamping (0.622);//6.22
      tissue.setStiffnessDamping (0.11);//0.11
      //tissue.setMaxStepSize (10 * TimeBase.MSEC);
      //tissue.setIntegrator (Integrator.ConstrainedBackwardEuler);
   }
}
