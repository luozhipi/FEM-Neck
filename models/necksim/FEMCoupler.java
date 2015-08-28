package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemModel.SurfaceRender;
import artisynth.core.femmodels.FemNode3d;
import artisynth.core.gui.NumericProbePanel;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.ArtisynthPath;
import artisynth.models.headneck.FEBioXML;

import maspack.geometry.OBBTree;
import maspack.geometry.PolygonalMesh;
import maspack.matrix.Vector3d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.PointStyle;
import maspack.render.RenderProps.Shading;
import maspack.util.ReaderTokenizer;

public class FEMCoupler {
   public MechModel myMechModel;
   protected ArrayList<FEMInfo> femInfoList = new ArrayList<FEMInfo>();
   private String femListFilename = "femlist.txt";
   public static String femPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   private class FEMInfo {
      public String musclename;
      public String filename;
      public String originMeshname;
      public String insertMeshname;
      boolean isPair;
      public void scan(ReaderTokenizer rtok) throws IOException {
         musclename = rtok.sval;
         rtok.nextToken();
         filename = rtok.sval;
         rtok.nextToken();
         if(rtok.sval.compareTo ("pair")==0)
            isPair = true;
         else
            isPair = false;
         rtok.nextToken();
         originMeshname = rtok.sval;
         rtok.nextToken();
         insertMeshname = rtok.sval;
      }
   }
   public FEMCoupler(MechModel mechModel) throws Exception
   {
      this.myMechModel = mechModel;
   }
   public void loadFEM() throws Exception
   {
      femInfoList.clear ();
      femInfoList = readFEMInfoList(femListFilename);
      for(FEMInfo feminfo : femInfoList)
      {
         CoupleByICP coupleByICP = new CoupleByICP();
         if(feminfo.isPair)
         {
            String lname = "l_"+feminfo.filename;
            FemModel3d ltissue = new FemModel3d ("l_"+feminfo.musclename);   
            MeshReader.read (
               ltissue, 5000, femPath + lname + ".1.node", femPath + lname
               + ".1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
            setTissueProps(ltissue);
            myMechModel.addModel (ltissue);
            MuscleComputingProps.setComputingProps (ltissue);
            if(feminfo.musclename.compareTo ("sternocleidomastoid")==0)
            {
               RigidBody c3 = myMechModel.rigidBodies ().get ("C3");
               myMechModel.setCollisionBehavior (ltissue, c3, true);
               RigidBody c4 = myMechModel.rigidBodies ().get ("C4");
               myMechModel.setCollisionBehavior (ltissue, c4, true);
               RigidBody c5 = myMechModel.rigidBodies ().get ("C5");
               myMechModel.setCollisionBehavior (ltissue, c5, true);
               RigidBody c6 = myMechModel.rigidBodies ().get ("C6");
               myMechModel.setCollisionBehavior (ltissue, c6, true);
            }
            else if(feminfo.musclename.compareTo ("thyrohyoid")==0)
            {
               RigidBody  thyroid= myMechModel.rigidBodies ().get ("thyroid");
               myMechModel.setCollisionBehavior (ltissue, thyroid, true);
            }
            String rname = "r_"+feminfo.filename;
            FemModel3d rtissue = new FemModel3d ("r_"+feminfo.musclename);   
            MeshReader.read (
               rtissue, 5000, femPath + rname + ".1.node", femPath + rname
               + ".1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
            setTissueProps(rtissue);
            myMechModel.addModel (rtissue);
            MuscleComputingProps.setComputingProps (rtissue);
            if(feminfo.musclename.compareTo ("sternocleidomastoid")==0)
            {
               RigidBody c3 = myMechModel.rigidBodies ().get ("C3");
               myMechModel.setCollisionBehavior (rtissue, c3, true);
               RigidBody c4 = myMechModel.rigidBodies ().get ("C4");
               myMechModel.setCollisionBehavior (rtissue, c4, true);
               RigidBody c5 = myMechModel.rigidBodies ().get ("C5");
               myMechModel.setCollisionBehavior (rtissue, c5, true);
               RigidBody c6 = myMechModel.rigidBodies ().get ("C6");
               myMechModel.setCollisionBehavior (rtissue, c6, true);
            }
            else if(feminfo.musclename.compareTo ("thyrohyoid")==0)
            {
               RigidBody  thyroid= myMechModel.rigidBodies ().get ("thyroid");
               myMechModel.setCollisionBehavior (rtissue, thyroid, true);
            }
         }
         coupleByICP.couple (myMechModel, feminfo.musclename, feminfo.filename);
      }
   }
   public void couple(FemModel3d tissue, FEMInfo feminfo, String femName) throws Exception
   {
      ArrayList<Integer> bcs = FEBioXML.read (femPath+femName+".feb", 1);
      for (int i =0; i< bcs.size (); i++)
      {
         if(!tissue.getNode (bcs.get (i)-1).isAttached ())
         {
            renderOverlap (tissue, bcs, 1, i);
            myMechModel.attachPoint(tissue.getNode (bcs.get (i)-1),myMechModel.rigidBodies ().get (feminfo.originMeshname));
         }
      }
      bcs = FEBioXML.read (femPath+femName+".feb", 2);
      for (int i =0; i< bcs.size (); i++)
      {
         if(!tissue.getNode (bcs.get (i)-1).isAttached ())
         {           
            renderOverlap (tissue, bcs, 2, i);
            myMechModel.attachPoint(tissue.getNode (bcs.get (i)-1),myMechModel.rigidBodies ().get (feminfo.insertMeshname));
         }
      }
   }
   public static void setTissueProps(FemModel3d tissue)
   {
      tissue.setSurfaceRendering (SurfaceRender.Shaded);
      RenderProps.setDrawEdges (tissue, false);
      RenderProps.setFaceColor (tissue, Color.red);
      RenderProps.setVisible (tissue, GlobalParameters.showMuscle);
      RenderProps.setShading (tissue, Shading.GOURARD);
      RenderProps.setLineColor (tissue, Color.blue);
      RenderProps.setLineWidth (tissue, 0);
      RenderProps.setPointColor (tissue, Color.white);
      RenderProps.setPointStyle (tissue, PointStyle.SPHERE);
      RenderProps.setPointRadius (tissue, 0*GlobalParameters.scale_to_human);
   }
   public void renderOverlap(FemModel3d tissue, ArrayList<Integer> bcs, int groupIdx, int idx)
   {
      FemNode3d node = (FemNode3d) tissue.getNode (bcs.get (idx)-1);
      RenderProps.setPointColor (node, getMuscleColor(groupIdx));
      RenderProps.setPointStyle (node, PointStyle.SPHERE);
      RenderProps.setPointRadius (node, 1.0*GlobalParameters.scale_to_human);
      RenderProps.setVisible (node, false);
   }
   public static Color getMuscleColor (int i) {
      return NumericProbePanel.colorList[i+1];
   }
   private ArrayList<FEMInfo> readFEMInfoList(String filename)
   throws IOException {
      ReaderTokenizer rtok = new ReaderTokenizer(new FileReader(ArtisynthPath
         .getSrcRelativePath(NeckSkeleton.class, "fem/" + filename)));
      rtok.wordChars(".");
      ArrayList<FEMInfo> bodyInfoList = new ArrayList<FEMInfo>();
      while (rtok.nextToken() != ReaderTokenizer.TT_EOF) {
         FEMInfo bi = new FEMInfo();
         bi.scan(rtok);
         bodyInfoList.add(bi);
      }
      return bodyInfoList;
   }

}
