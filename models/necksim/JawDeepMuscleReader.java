package artisynth.models.necksim;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import maspack.matrix.Point3d;
import maspack.matrix.Vector3d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.LineStyle;
import maspack.render.RenderProps.Shading;
import maspack.util.ReaderTokenizer;
import artisynth.core.mechmodels.ExcitationComponent;
import artisynth.core.mechmodels.FrameMarker;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.Muscle;
import artisynth.core.mechmodels.Muscle.MuscleType;
import artisynth.core.mechmodels.MuscleExciter;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.AmiraLandmarkReader;
import artisynth.core.util.ArtisynthPath;

public class JawDeepMuscleReader{

   public MechModel myMechModel;
   public HashMap<String, MuscleInfo> muscleInfoList = new HashMap<String, MuscleInfo>();
   public ArrayList<String> muscleList = new ArrayList<String>();
   public static ArrayList<String> myMuscles = new ArrayList<String>();
   public ArrayList<String> myExcitors = new ArrayList<String>();
   private String filename = "jawdeepmuscle.txt";
   
   String[] bi_openers = {
                          "uni_c_mne022",
                          "bi_l_mne0220",
                          "bi_l_mne0221",
                          "bi_l_mne0222",
                          "bi_l_mne021_a",
                          };
   
   public class MuscleInfo {
      public String name;
      public String origin;
      public String insertion;

      String pairedFlag; // true == left-right paired muscle

      public String isPaired() {
         return pairedFlag;
      }

      public void scan(ReaderTokenizer rtok) throws IOException {
         name = rtok.sval;
         rtok.nextToken();
         origin = rtok.sval;
         rtok.nextToken();
         insertion = rtok.sval;
         rtok.nextToken();
         pairedFlag = rtok.sval;
      }

   }

   public JawDeepMuscleReader(MechModel mechModel)
   {
      this.myMechModel = mechModel;
   }
   public void assemble() throws Exception
   {
      readJawMuscleInfoList();
      assemOpenMuscle();
      assemOtherMuscle();
      assembleMuscleGroups();
   }
   public void assemOtherMuscle() throws Exception
   {
      createOthreMuscle("l_mne021_b");
      createOthreMuscle("l_mne018");
   }
   public void createOthreMuscle(String name) throws Exception
   {
      Point3d[] pts;
      pts = AmiraLandmarkReader.read(ArtisynthPath.getSrcRelativePath(
         JawDeepMuscleReader.class, "def/" + name + ".landmarkAscii"),
         GlobalParameters.scale_to_human);
      RigidBody origin = myMechModel.getMyRigidBodies().get("skull");
      RigidBody insertion = myMechModel.getMyRigidBodies().get("hyoid");

      Point3d ptsi2 = new Point3d(pts[0].x, pts[0].y, pts[0].z);
      Point3d ptsi21 = new Point3d(pts[1].x, pts[1].y, pts[1].z);
      Vector3d optLV =  new Vector3d();
      optLV.sub(ptsi2,  ptsi21);
      double optLen = optLV.norm ();
      ptsi2.sub (origin.getPosition ());
      ptsi21.sub (insertion.getPosition ());
      Point3d[] ptsnew =new Point3d[2];
      ptsnew[0] = ptsi2;
      ptsnew[1] = ptsi21;
      double maxL = 30*GlobalParameters.scale_to_human + optLen;
      createMuscle(name, MuscleType.Peck, 19.8, 0.0, optLen, maxL, 0.0, 0.0, ptsnew, origin, insertion, true);
   }
   public void assemOpenMuscle() throws Exception
   {
      createOpenMuscle("l_mne022");
      createOpenMuscle("l_mne021_a");
      createOpenMuscle("c_mne022");
   }
   public void assembleMuscleGroups() {
      MuscleExciter open = new MuscleExciter("bi_open");
      for (int i = 0; i< bi_openers.length; i++) {
         String target = bi_openers[i];
         ExcitationComponent c = (MuscleExciter)myMechModel.getMuscleExciters ().get (target);
         open.addTarget(c, 1.0);
      }
      myMechModel.addMuscleExciter(open);
   }
   public void createOpenMuscle(String name) throws IOException
   {
      Point3d[] pts;
      pts = AmiraLandmarkReader.read(ArtisynthPath.getSrcRelativePath(
         JawDeepMuscleReader.class, "def/" + name + ".landmarkAscii"),
         GlobalParameters.scale_to_human);
      RigidBody origin = myMechModel.getMyRigidBodies().get("jaw");
      RigidBody insertion = myMechModel.getMyRigidBodies().get("hyoid");
      for(int i = 0; i< pts.length/2; i++)
      {
         Point3d ptsi2 = new Point3d(pts[i*2].x, pts[i*2].y, pts[i*2].z);
         Point3d ptsi21 = new Point3d(pts[i*2+1].x, pts[i*2+1].y, pts[i*2+1].z);
         Vector3d optLV =  new Vector3d();
         optLV.sub(ptsi2,  ptsi21);
         double optLen = optLV.norm ();
         ptsi2.sub (origin.getPosition ());
         ptsi21.sub (insertion.getPosition ());
         Point3d[] ptsnew =new Point3d[2];
         ptsnew[0] = ptsi2;
         ptsnew[1] = ptsi21;
         double maxL = 30*GlobalParameters.scale_to_human + optLen;
         if(name.compareTo ("c_mne022")==0)
            createMuscle(name, MuscleType.Peck, 35.8, 0.0, optLen, maxL, 0.0, 0.0, ptsnew, origin, insertion, false);
         else if(name.compareTo ("l_mne022")==0)
            createMuscle(name+i, MuscleType.Peck, 35.8, 0.0, optLen, maxL, 0.0, 0.0, ptsnew, origin, insertion, true);
         else
            createMuscle(name, MuscleType.Peck, 35.8, 0.0, optLen, maxL, 0.0, 0.0, ptsnew, origin, insertion, true);
      }
   }
   public void createMuscles(String name, MuscleType muscleType, double maxF, double damping, 
                             double maxL, double ratio, double passiveF) throws Exception
   {
      MuscleInfo info = muscleInfoList.get(name);
      RigidBody origin = myMechModel.getMyRigidBodies().get(info.origin);
      RigidBody insertion = myMechModel.getMyRigidBodies().get(info.insertion);
      Point3d[] pts = AmiraLandmarkReader.read(ArtisynthPath.getSrcRelativePath(
         JawDeepMuscleReader.class, "def/" + name + ".landmarkAscii"),
         GlobalParameters.scale_to_human);
      Point3d pts0 = new Point3d(pts[0].x, pts[0].y, pts[0].z);
      Point3d pts1 = new Point3d(pts[1].x, pts[1].y, pts[1].z);
      Vector3d optLV = new Vector3d();
      optLV.sub (pts0, pts1);
      double optLen = optLV.norm ();
      maxL +=optLen;
      Point3d[] ptsnew = new Point3d[2];
      pts0.sub (origin.getPosition ());
      ptsnew[0]= pts0;
      pts1.sub (insertion.getPosition ());
      ptsnew[1]= pts1; 
      createMuscle(name, muscleType, maxF, damping, optLen, maxL, ratio, passiveF, ptsnew, origin, insertion, true);
   }
   public void createMuscle(String name, MuscleType muscleType, double maxF, double damping, double optLen, 
                            double maxL, double ratio, double passiveF, Point3d[] pts, RigidBody origin, RigidBody insertion,
                            boolean pairFlag)
   {

      myMechModel.addFrameMarker(new FrameMarker(name + "_origin"), origin,
         pts[0]);
      myMechModel.addFrameMarker(new FrameMarker(name + "_insertion"), insertion,
         pts[1]);
      Muscle m = new Muscle();
      m.setMuscleType (muscleType);
      m.setTendonRatio (ratio);
      m.setOptLength (optLen);
      m.setMaxLength (maxL);
      m.setMaxForce (maxF);
      m.setPassiveFraction (passiveF);
      m.setName(name);
      m.setFirstPoint(myMechModel.getMyFrameMarkers().get(name + "_origin"));
      m.setSecondPoint(myMechModel.getMyFrameMarkers().get(name + "_insertion"));
      RenderProps.setVisible (myMechModel.getMyFrameMarkers().get(name + "_origin"), false);
      RenderProps.setVisible (myMechModel.getMyFrameMarkers().get(name + "_insertion"), false);
      m.setDamping(damping);
      m.setForceScaling (1000);
      myMechModel.addAxialSpring(m);
      myMuscles.add (name);
      setRender(m);
      if(pairFlag)
      {
         Point3d pts0 = createRightSidePoint(pts[0]);
         Point3d pts1 = createRightSidePoint(pts[1]);
         myMechModel.addFrameMarker(new FrameMarker("r_"+name + "_origin"), origin,
            pts0);
         myMechModel.addFrameMarker(new FrameMarker("r_"+name + "_insertion"), insertion,
            pts1);
         m = new Muscle();
         m.setMuscleType (muscleType);
         m.setTendonRatio (ratio);
         m.setOptLength (optLen);
         m.setMaxLength (maxL);
         m.setMaxForce (maxF);
         m.setPassiveFraction (passiveF);
         m.setName ("r_"+name);
         m.setFirstPoint(myMechModel.getMyFrameMarkers().get("r_"+name + "_origin"));
         m.setSecondPoint(myMechModel.getMyFrameMarkers().get("r_"+name + "_insertion"));
         RenderProps.setVisible (myMechModel.getMyFrameMarkers().get("r_"+name + "_origin"), false);
         RenderProps.setVisible (myMechModel.getMyFrameMarkers().get("r_"+name + "_insertion"), false);
         m.setDamping(damping);
         m.setForceScaling (1000);
         myMechModel.addAxialSpring(m);
         myMuscles.add (name);
         setRender(m);
      }
      String excitorName = "bi_" + name;
      if(!pairFlag)
      {
         excitorName = "uni_"+name;
      }
      MuscleExciter bilateral = new MuscleExciter(excitorName);
      Muscle left = (Muscle)myMechModel.axialSprings ().get (name);
      if(pairFlag)
      {
         Muscle right = (Muscle)myMechModel.axialSprings ().get ("r_"+name);
         bilateral.addTarget(right, 1.0);
      }
      bilateral.addTarget(left, 1.0);
      myMechModel.addMuscleExciter(bilateral);
      myExcitors.add (excitorName);
   }
   public void setRender(Muscle m)
   {
      RenderProps.setLineColor (m, Color.red);
      RenderProps.setLineStyle (m, LineStyle.ELLIPSOID);
      RenderProps.setLineRadius (m,  1.0*GlobalParameters.scale_to_human);
      RenderProps.setShading(m, Shading.GOURARD);
      RenderProps.setVisible (m, GlobalParameters.showhill);
   }
   public Point3d createRightSidePoint(Point3d leftSidePt) {
      Point3d rightSidePt = new Point3d();
      if (leftSidePt != null) {
         rightSidePt.set(leftSidePt);
         rightSidePt.x = -rightSidePt.x; // right-left mirrored in x-axis
      }
      return rightSidePt;
   }
   private void checkOriginInsertion(Point3d[] markerPts) {
      if (markerPts[0].z < markerPts[1].z) { // origin and insertion point are
         // mixed up, do swap
         Point3d tmp = markerPts[0];
         markerPts[0] = markerPts[1];
         markerPts[1] = tmp;
      }
   }
   public HashMap<String, MuscleInfo> readJawMuscleInfoList()
   throws IOException {
      HashMap<String, MuscleInfo> infoList = new HashMap<String, MuscleInfo>();
      ReaderTokenizer rtok = new ReaderTokenizer(new FileReader(ArtisynthPath
         .getSrcRelativePath(JawDeepMuscleReader.class, "def/" + filename)));
      while (rtok.nextToken() != ReaderTokenizer.TT_EOF) {
         MuscleInfo mi = new MuscleInfo();
         mi.scan(rtok);
         if(!infoList.containsKey (mi.name))
         {
            infoList.put(mi.name, mi);
            muscleList.add (mi.name);
         }
      }
      muscleInfoList = infoList;
      return muscleInfoList;
   }

}
