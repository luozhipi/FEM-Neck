package artisynth.models.necksim;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;

import maspack.matrix.Point3d;
import maspack.matrix.RigidTransform3d;
import maspack.matrix.Vector3d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.LineStyle;
import artisynth.core.mechmodels.AxialSpring;
import artisynth.core.mechmodels.FrameMarker;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RevoluteJoint;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.AmiraLandmarkReader;
import artisynth.core.util.ArtisynthPath;

public class CricoThyroidJoint{
 
   protected static MechModel mechModel;
   private static boolean fixedLaryngeal = false;
   public static void createJoint(MechModel myMechModel)
   {
      mechModel = myMechModel;
      if (mechModel.rigidBodies().get("cricoid") != null && !fixedLaryngeal) {
         attachMembraneMesh("hyoid", "thyroid", "thMem"); 
         attachMembraneMesh("cricoid", "base", "ctrMem");
         // membrane
         addPharynxSprings();
      } else {
         setLaryngealBodiesFixed();
      }
      addCricothyroidJoint();
   }
   public static void addCricothyroidJoint() {
      RigidBody thyroid = mechModel.rigidBodies().get("thyroid");
      RigidBody cricoid = mechModel.rigidBodies().get("cricoid");
      if (thyroid == null || cricoid == null) {
         System.err.println("Unable to add cricothyroid joint.");
         return;
      }
      RigidTransform3d XCA = new RigidTransform3d();
      XCA.p.set(GlobalParameters.cricothryroidArticulation);
      XCA.R.setAxisAngle(0, 1, 0, Math.PI / 2);
      RigidTransform3d XCW = new RigidTransform3d();
      RigidTransform3d XDW = new RigidTransform3d();
      XDW.p.set (GlobalParameters.cricothryroidArticulation);
      XDW.R.setAxisAngle (Vector3d.Y_UNIT, Math.toRadians (90));
      RevoluteJoint ctJoint = addRevoluteJoint (cricoid, thyroid, XDW);
      //RevoluteJoint ctJoint = new RevoluteJoint();
      ctJoint.setName("cricothyroid");
      Point3d pmin = new Point3d();
      Point3d pmax = new Point3d();
      thyroid.updateBounds(pmin, pmax);
      ctJoint.setAxisLength(1.0 * (pmax.x - pmin.x));
      if (ctJoint != null) {
         RenderProps.setLineSlices(ctJoint, 12);
         RenderProps.setLineColor(ctJoint, Color.ORANGE);
         RenderProps.setLineRadius(ctJoint, 2*GlobalParameters.scale_to_human);
         RenderProps.setVisible (ctJoint, GlobalParameters.showas);
      }
     // ctJoint.setBodies(cricoid, XCA, thyroid, XCA);
     // mechModel.addRigidBodyConnector(ctJoint);
      ctJoint.setMaxTheta (5);
      ctJoint.setMinTheta (-5);
   }
   public static RevoluteJoint addRevoluteJoint (
      RigidBody bodyA, RigidBody bodyB, RigidTransform3d XDW) {
      RigidTransform3d XFA = new RigidTransform3d();
      RigidTransform3d XDB = new RigidTransform3d();

      XDB.mulInverseLeft (bodyB.getPose(), XDW);
      XFA.mulInverseLeft (bodyA.getPose(), XDW);
      RevoluteJoint joint = new RevoluteJoint (bodyA, XFA, bodyB, XDB);
      joint.setMaxTheta (135);
      joint.setMinTheta (-160);
      mechModel.addRigidBodyConnector (joint);
      return joint;
   }
   public static void setLaryngealBodiesFixed() {
      String[] larNames = new String[] { "hyoid", "thyroid", "cricoid" };
      RigidBody body;
      for (int i = 0; i < larNames.length; i++) {
         if ((body = mechModel.getMyRigidBodies().get(larNames[i])) != null) {
            body.setDynamic(false);
            RenderProps.setVisible (body, false);
         }
      }
   }
   static final double VERTICAL_TRACHEA_OFFSET = 2.0*GlobalParameters.scale_to_human;
   public static void doTracheaOffset(Point3d[] pts) {
      // add vertical offset to all odd points
      int count = 0;
      for (Point3d p : pts) {
         if (count % 2 == 1) {
            p.y = p.y - VERTICAL_TRACHEA_OFFSET;
         }
         count++;
      }
   }
   public static void attachMembraneMesh(String oBodyName, String iBodyName,
      String memName) {
      RigidBody oBody = mechModel.getMyRigidBodies().get(oBodyName);
      RigidBody iBody = mechModel.getMyRigidBodies().get(iBodyName);
      if (oBody == null || iBody == null) {
         System.err.println("attachMembraneMesh: " + memName
            + " not attached because body null.");
         return;
      }
      Point3d[] pts = null;
      try {
         pts = AmiraLandmarkReader.read(ArtisynthPath.getSrcRelativePath(
            CricoThyroidJoint.class, "def/" + memName + ".landmarkAscii"),
            GlobalParameters.scale_to_human);
      } catch (IOException e) {
         e.printStackTrace();
      }

      if (memName.compareTo("ctrMem") == 0.0) {
         doTracheaOffset(pts);
      }
      int num = 0;
      for (int k = 0; k < pts.length; k += 2) {
         Point3d originPnt = new Point3d(pts[k].x, pts[k].y, pts[k].z);
         if(k%2==0)
            originPnt.sub (oBody.getPosition ());
         else
            originPnt.sub (iBody.getPosition ());
         Point3d insertPnt = new Point3d(pts[k+1].x, pts[k+1].y, pts[k+1].z);
         if((k+1)%2==0)
            insertPnt.sub (oBody.getPosition ());
         else
            insertPnt.sub (iBody.getPosition ());
         addMembraneStrand("l" + memName + num++, originPnt, insertPnt, oBody,
            iBody);
         if (k + 2 < pts.length)
         {
            originPnt =  new Point3d(pts[k].x, pts[k].y, pts[k].z);
            if(k%2==0)
               originPnt.sub (oBody.getPosition ());
            else
               originPnt.sub (iBody.getPosition ());
            insertPnt = new Point3d(pts[k+3].x, pts[k+3].y, pts[k+3].z);
            if((k+3)%2==0)
               insertPnt.sub (oBody.getPosition ());
            else
               insertPnt.sub (iBody.getPosition ());
            addMembraneStrand("l" + memName + num++, originPnt, insertPnt, oBody,
               iBody);
            originPnt =  new Point3d(pts[k+2].x, pts[k+2].y, pts[k+2].z);
            if((k+2)%2==0)
               originPnt.sub (oBody.getPosition ());
            else
               originPnt.sub (iBody.getPosition ());
            insertPnt = new Point3d(pts[k+1].x, pts[k+1].y, pts[k+1].z);
            if((k+1)%2==0)
               insertPnt.sub (oBody.getPosition ());
            else
               insertPnt.sub (iBody.getPosition ());
            addMembraneStrand("l" + memName + num++, originPnt, insertPnt,
               oBody, iBody);
         }
      }
      num = 0;
      for (int k = 0; k < pts.length; k += 2) {
         Point3d originPnt = createRightSidePoint(pts[k]);
         if(k%2==0)
            originPnt.sub (oBody.getPosition ());
         else
            originPnt.sub (iBody.getPosition ());
         Point3d insertPnt = createRightSidePoint(pts[k + 1]);
         if((k+1)%2==0)
            insertPnt.sub (oBody.getPosition ());
         else
            insertPnt.sub (iBody.getPosition ());
         addMembraneStrand("r" + memName + num++, originPnt,
            insertPnt, oBody, iBody);
         if (k + 2 < pts.length) {
            originPnt = createRightSidePoint(pts[k]);
            if(k%2==0)
               originPnt.sub (oBody.getPosition ());
            else
               originPnt.sub (iBody.getPosition ());
            insertPnt = createRightSidePoint(pts[k + 3]);
            if((k+3)%2==0)
               insertPnt.sub (oBody.getPosition ());
            else
               insertPnt.sub (iBody.getPosition ());
            addMembraneStrand("r" + memName + num++,
               originPnt,
               insertPnt, oBody, iBody);
            originPnt = createRightSidePoint(pts[k+2]);
            if((k+2)%2==0)
               originPnt.sub (oBody.getPosition ());
            else
               originPnt.sub (iBody.getPosition ());
            insertPnt = createRightSidePoint(pts[k + 1]);
            if((k+1)%2==0)
               insertPnt.sub (oBody.getPosition ());
            else
               insertPnt.sub (iBody.getPosition ());
            addMembraneStrand("r" + memName + num++,
               originPnt,
               insertPnt, oBody, iBody);
         }
      }
   }
   public static Point3d createRightSidePoint(Point3d leftSidePt) {
      Point3d rightSidePt = new Point3d();
      if (leftSidePt != null) {
         rightSidePt.set(leftSidePt);
         rightSidePt.x = -rightSidePt.x; // right-left mirrored in x-axis
      }
      return rightSidePt;
   }

   
   static final double MEMBRANE_CYL_RADIUS = 0.5*GlobalParameters.scale_to_human;
   static final double MEMBRANE_PT_RADIUS = 0.5*GlobalParameters.scale_to_human;
   
   protected static ArrayList<AxialSpring> memList = new ArrayList<AxialSpring>();
   protected static ArrayList<FrameMarker> memPts = new ArrayList<FrameMarker>(); 
   private static void addMembraneStrand(String name, Point3d originPt,
      Point3d insertionPt, RigidBody oBody, RigidBody iBody) {
      FrameMarker origin;
      FrameMarker insertion;
      AxialSpring membraneStrand;
      if (oBody == null || iBody == null) {
         System.err.println("addMembraneStrand Error: rigidbody args null");
         return;
      }
      Vector3d disp = new Vector3d();
      // render props for markers
      RenderProps memPointProps = RenderProps.createPointProps(null);
      memPointProps.setPointColor(Color.green);
      memPointProps.setPointRadius(MEMBRANE_PT_RADIUS);
      memPointProps.setVisible (false);

      origin = new FrameMarker(originPt);
      origin.setName(name + "_origin");
      origin.setRenderProps(memPointProps.clone());
      origin.setFrame(oBody);
      mechModel.getMyFrameMarkers().add(origin);
      insertion = new FrameMarker(insertionPt);
      insertion.setName(name + "_insertion");
      insertion.setRenderProps(memPointProps.clone());
      insertion.setFrame(iBody);
      mechModel.getMyFrameMarkers().add(insertion);

      disp.sub(origin.getPosition(), insertion.getPosition());
      if (name.contains("thMem")) { 
         membraneStrand = new AxialSpring(name, GlobalParameters.thMemStiff, GlobalParameters.thMemDamp, disp
            .norm());
      } else { 
         membraneStrand = new AxialSpring(name, GlobalParameters.ctrMemStiff, GlobalParameters.ctrMemDamp, disp
            .norm());
      }
      RenderProps.setLineStyle(membraneStrand, LineStyle.CYLINDER);
      RenderProps.setLineRadius(membraneStrand, MEMBRANE_CYL_RADIUS);
      RenderProps.setLineColor(membraneStrand, Color.green);
      RenderProps.setVisible (membraneStrand, GlobalParameters.showas);
      membraneStrand.setFirstPoint(origin);
      membraneStrand.setSecondPoint(insertion);
      mechModel.addAxialSpring(membraneStrand);
      memPts.add(origin);
      memPts.add(insertion);
      memList.add(membraneStrand);

   }
   public static void addPharynxSprings() {
      RigidBody base = mechModel.getMyRigidBodies().get("base");
      RigidBody hyoid = mechModel.getMyRigidBodies().get("hyoid");
      if (hyoid == null || base == null) return;
      for (int i = 0; i < 2; i++) {
         // i == 0 is left-side, i == 1 is right-side
         FrameMarker origin = new FrameMarker();
         FrameMarker insertion = new FrameMarker();
         RenderProps.setVisible (origin, false);
         RenderProps.setVisible (insertion, false);
         Point3d originPnt = new Point3d(i == 0 ? 9.086*GlobalParameters.scale_to_human: -9.939*GlobalParameters.scale_to_human,
            1726.271*GlobalParameters.scale_to_human, 63.020*GlobalParameters.scale_to_human);
         originPnt.sub (hyoid.getPosition ());
         mechModel.addFrameMarker(origin, hyoid, originPnt);
         Point3d insertPnt = new Point3d(
            i == 0 ? 13.655*GlobalParameters.scale_to_human: -13.139*GlobalParameters.scale_to_human, 1742.023*GlobalParameters.scale_to_human, 34.616*GlobalParameters.scale_to_human);
         insertPnt.sub (base.getPosition ());
         mechModel.addFrameMarker(insertion, base, insertPnt);
         String name = (i == 0 ? "l" : "r") + "phc";
         origin.setName(name + "_origin");
         insertion.setName(name + "_insertion");
         Vector3d disp = new Vector3d();
         disp.sub(origin.getPosition(), insertion.getPosition());
         AxialSpring constrictor = new AxialSpring(name,
            GlobalParameters.defaultPharnyxStiffness, GlobalParameters.pharnyxDamp, disp.norm ());
         constrictor.setFirstPoint(origin);
         constrictor.setSecondPoint(insertion);
         RenderProps.setLineStyle(constrictor, LineStyle.CYLINDER);
         RenderProps.setLineRadius(constrictor,
               MEMBRANE_CYL_RADIUS);
         RenderProps.setLineColor(constrictor, Color.green);
         RenderProps.setVisible (constrictor, GlobalParameters.showas);
         mechModel.addAxialSpring(constrictor);
      }
   }
}
