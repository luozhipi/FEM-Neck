package artisynth.models.necksim;

import java.awt.Color;

import maspack.matrix.Point3d;
import maspack.matrix.RigidTransform3d;
import maspack.render.RenderProps;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.mechmodels.SphericalJoint;
import artisynth.core.mechmodels.SphericalRpyJoint;

public class SpineJoints{
   protected static MechModel mechModel;
   public static void createJoints(MechModel myMechModel)
   {
      mechModel = myMechModel;
      addCervialJoints();
   }
   public static void addCervialJoints()
   {
      RigidBody skull = mechModel.rigidBodies ().get ("skull");
      RigidBody jaw = mechModel.rigidBodies ().get ("jaw");
      RigidBody c1 = mechModel.rigidBodies ().get ("C1");
      RigidBody c2 = mechModel.rigidBodies ().get ("C2");
      RigidBody c3 = mechModel.rigidBodies ().get ("C3");
      RigidBody c4 = mechModel.rigidBodies ().get ("C4");
      RigidBody c5 = mechModel.rigidBodies ().get ("C5");
      RigidBody c6 = mechModel.rigidBodies ().get ("C6");
      RigidBody c7 = mechModel.rigidBodies ().get ("C7");
      RigidBody base = mechModel.rigidBodies ().get ("base");
           
      RigidTransform3d XDW = new RigidTransform3d();
      XDW.R.setRpy (0, 0,  0);
      
      Point3d pivot = GlobalParameters.c7_base_joint;
      SphericalRpyJoint joint01 = addSphericalRpyJoint ("c7-base",pivot, base, c7, XDW);
      joint01.setRollRange (-90, 90);
      joint01.setPitchRange (-45, 45);
      joint01.setYawRange (-90, 90);
      
      pivot = GlobalParameters.c6_c7_joint;
      SphericalRpyJoint joint02 = addSphericalRpyJoint ("c6-c7",pivot, c7, c6, XDW);
      joint02.setRollRange (-90, 90);
      joint02.setPitchRange (-45, 45);
      joint02.setYawRange (-90, 90);
      
      pivot = GlobalParameters.c5_c6_joint;
      SphericalRpyJoint joint03 = addSphericalRpyJoint ("c5-c6",pivot, c6, c5, XDW);
      joint03.setRollRange (-90, 90);
      joint03.setPitchRange (-45, 45);
      joint03.setYawRange (-90, 90);

      pivot = GlobalParameters.c4_c5_joint;
      SphericalRpyJoint joint04 = addSphericalRpyJoint ("c4-c5",pivot, c5, c4, XDW);
      joint04.setRollRange (-90, 90);
      joint04.setPitchRange (-45, 45);
      joint04.setYawRange (-90, 90);

      pivot = GlobalParameters.c3_c4_joint;
      SphericalRpyJoint joint05 = addSphericalRpyJoint ("c3-c4",pivot, c4, c3, XDW);
      joint05.setRollRange (-90, 90);
      joint05.setPitchRange (-45, 45);
      joint05.setYawRange (-90, 90);

      pivot = GlobalParameters.c2_c3_joint;
      SphericalRpyJoint joint06 = addSphericalRpyJoint ("c2-c3",pivot, c3, c2, XDW);
      joint06.setRollRange (-90, 90);
      joint06.setPitchRange (-45, 45);
      joint06.setYawRange (-90, 90);
      
      pivot = GlobalParameters.c1_c2_joint;
      SphericalRpyJoint joint07 = addSphericalRpyJoint ("c1-c2", pivot, c2, c1, XDW);
      joint07.setRollRange (-90, 90);
      joint07.setPitchRange (-45, 45);
      joint07.setYawRange (-90, 90);

      pivot  = GlobalParameters.skull_c1_joint;
      SphericalRpyJoint joint08 = addSphericalRpyJoint ("skull-c1", pivot, c1, skull, XDW);
      joint08.setRollRange (-90, 90);
      joint08.setPitchRange (-45, 45);
      joint08.setYawRange (-90, 90);
      
      pivot  = GlobalParameters.skull_jaw_joint;
      SphericalRpyJoint joint09 = addSphericalRpyJoint ("skull-jaw", pivot, jaw, skull, XDW);
      joint09.setRollRange (-90, 90);
      joint09.setPitchRange (-45, 45);
      joint09.setYawRange (-90, 90);
      
   }
   public static SphericalRpyJoint addSphericalRpyJoint (String name, Point3d pivot,
      RigidBody bodyA, RigidBody bodyB, RigidTransform3d XDW) {
      XDW.p.set (pivot);
      RigidTransform3d XFA = new RigidTransform3d();
      RigidTransform3d XDB = new RigidTransform3d();
      XDB.mulInverseLeft (bodyB.getPose(), XDW);
      XFA.mulInverseLeft (bodyA.getPose(), XDW);
      SphericalRpyJoint joint = new SphericalRpyJoint (bodyA, XFA, bodyB, XDB);
      joint.setName (name);
      rendreJoint(joint);
      mechModel.addRigidBodyConnector (joint);
      return joint;
   }
   public static void rendreJoint(SphericalRpyJoint joint)
   {
      RenderProps.setPointStyle (joint, RenderProps.PointStyle.SPHERE);
      RenderProps.setPointColor (joint, Color.blue);
      RenderProps.setPointRadius (joint, 5*GlobalParameters.scale_to_human);
      joint.setAxisLength (10*GlobalParameters.scale_to_human);
      RenderProps.setVisible (joint, false);
   }
}
