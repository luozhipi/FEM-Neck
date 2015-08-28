package artisynth.models.necksim;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import maspack.geometry.PolygonalMesh;
import maspack.matrix.Point3d;
import maspack.matrix.RigidTransform3d;
import maspack.matrix.Vector3d;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.ArtisynthPath;

public class BodyPos{
   
   protected static MechModel mechModel;
   
   public static void setPos(MechModel myMechModel) throws Exception
   {
      mechModel = myMechModel;
      //initBodyPos();
      setPose();
   }
   protected static void initBodyPos() throws Exception {
      Point3d rpy = new Point3d(0,0,0);

      RigidBody body = mechModel.rigidBodies ().get ("jaw");
      GlobalParameters.jawPivot.scale (-1);
      transformGeo(body, GlobalParameters.jawPivot,rpy);

      PrintStream ps =
      new PrintStream (
         new FileOutputStream (ArtisynthPath.getHomeRelativeFile ("src/artisynth/models/necksim/rigid/jawnew.obj", "."))
      );
      PolygonalMesh mesh  = body.getMesh ();
      RigidTransform3d X = new RigidTransform3d();
      X.p.set (GlobalParameters.jawPivot);
      X.R.setRpy (rpy.x, rpy.y, rpy.z);
      mesh.transform (X);
      mesh.write (ps, "%g");

//      body = mechModel.rigidBodies ().get ("C1");
//      GlobalParameters.c1Pivot.scale (-1);
//      transformGeo (body, GlobalParameters.c1Pivot,rpy);
//
//      body = mechModel.rigidBodies ().get ("C2"); 
//      GlobalParameters.c2Pivot.scale (-1);
//      transformGeo (body, GlobalParameters.c2Pivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("C3");
//      GlobalParameters.c3Pivot.scale (-1);
//      transformGeo (body, GlobalParameters.c3Pivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("C4");
//      GlobalParameters.c4Pivot.scale(-1);
//      transformGeo (body, GlobalParameters.c4Pivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("C5");
//      GlobalParameters.c5Pivot.scale (-1);
//      transformGeo (body, GlobalParameters.c5Pivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("C6");
//      GlobalParameters.c6Pivot.scale (-1);
//      transformGeo (body, GlobalParameters.c6Pivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("C7");
//      GlobalParameters.c7Pivot.scale(-1);
//      transformGeo (body, GlobalParameters.c7Pivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("cricoid");
//      GlobalParameters.cricoidPivot.scale (-1);
//      transformGeo (body, GlobalParameters.cricoidPivot,rpy);
//
//      body = mechModel.rigidBodies ().get ("thyroid");
//      GlobalParameters.thyroidPivot.scale (-1);
//      transformGeo (body, GlobalParameters.thyroidPivot,rpy);
//
//
//      body = mechModel.rigidBodies ().get ("hyoid");
//      GlobalParameters.hyoidPivot.scale (-1);
//      transformGeo (body, GlobalParameters.hyoidPivot,rpy);
   }
   protected static void setPose()
   {
      Point3d rpy = new Point3d(0,0,0);
      
      RigidBody body = mechModel.rigidBodies ().get ("base");
      Point3d basePivotcopy = GlobalParameters.basePivot;
      setBodyPose(body, basePivotcopy,rpy);
      
      body = mechModel.rigidBodies ().get ("skull");
      Point3d skullPivotcopy = GlobalParameters.skullPivot;
      setBodyPose(body, skullPivotcopy,rpy);
      
      body = mechModel.rigidBodies ().get ("jaw");
      Point3d jawPivotcopy = GlobalParameters.jawPivot;
      setBodyPose(body, jawPivotcopy,rpy);

      body = mechModel.rigidBodies ().get ("C1");
      Point3d c1Pivotcopy = GlobalParameters.c1Pivot;
      setBodyPose (body, c1Pivotcopy,rpy);

      body = mechModel.rigidBodies ().get ("C2");
      Point3d c2Pivotcopy = GlobalParameters.c2Pivot;
      setBodyPose (body, c2Pivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("C3");
      Point3d c3Pivotcopy = GlobalParameters.c3Pivot;
      setBodyPose (body, c3Pivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("C4");
      Point3d c4Pivotcopy = GlobalParameters.c4Pivot;
      setBodyPose (body, c4Pivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("C5");
      Point3d c5Pivotcopy = GlobalParameters.c5Pivot;
      setBodyPose (body, c5Pivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("C6");
      Point3d c6Pivotcopy = GlobalParameters.c6Pivot;
      setBodyPose (body, c6Pivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("C7");
      Point3d c7Pivotcopy = GlobalParameters.c7Pivot;
      setBodyPose (body, c7Pivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("cricoid");
      Point3d CricoidPivotcopy = GlobalParameters.cricoidPivot;
      setBodyPose (body, CricoidPivotcopy,rpy);

      body = mechModel.rigidBodies ().get ("thyroid");
      Point3d thyroidPivotcopy = GlobalParameters.thyroidPivot;
      setBodyPose (body, thyroidPivotcopy,rpy);


      body = mechModel.rigidBodies ().get ("hyoid");
      Point3d hyoidPivotcopy = GlobalParameters.hyoidPivot;
      setBodyPose (body, hyoidPivotcopy,rpy);
   }
   public static void transformGeo(RigidBody body, Point3d pivot,Point3d rpy)
   {
      RigidTransform3d X = new RigidTransform3d();
      X.p.set (pivot);
      X.R.setRpy (rpy.x, rpy.y, rpy.z);
      body.transformGeometry (X);
   }
   protected static void setBodyPose (
      RigidBody body, Point3d pivot,Point3d rpy) {
      RigidTransform3d X = new RigidTransform3d();
      X.p.set (pivot.x, pivot.y, pivot.z);
      X.R.setRpy (Math.toRadians (rpy.x),
         Math.toRadians (rpy.y),
         Math.toRadians (rpy.z));
      body.setPose (X);
   }
}
