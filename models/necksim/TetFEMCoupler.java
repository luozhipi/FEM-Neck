package artisynth.models.necksim;

import java.awt.Color;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemModel.SurfaceRender;
import artisynth.core.femmodels.TetGenReader;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.util.ArtisynthPath;

import maspack.matrix.Vector3d;
import maspack.render.RenderProps;
import maspack.render.RenderProps.PointStyle;
import maspack.render.RenderProps.Shading;
import maspack.util.ReaderTokenizer;

public class TetFEMCoupler {
   public MechModel myMechModel;
   protected ArrayList<FEMInfo> femInfoList = new ArrayList<FEMInfo>();
   private String femListFilename = "tetfemlist.txt";
   public static String femPath =
   ArtisynthPath.getHomeRelativePath (
      "src/artisynth/models/necksim/fem/", ".");
   private class FEMInfo {
      public String musclename;
      public String filename;
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
      }
   }
   public TetFEMCoupler(MechModel mechModel) throws Exception
   {
      this.myMechModel = mechModel;
   }
   public void loadFEM() throws Exception
   {
      femInfoList.clear ();
      CoupleByICP coupleByICP = new CoupleByICP();
      femInfoList = readFEMInfoList(femListFilename);
      for(FEMInfo feminfo : femInfoList)
      {
         if(feminfo.isPair)
         {
            String lname = "l_"+feminfo.filename;
            FemModel3d ltissue = new FemModel3d ("l_"+feminfo.musclename);   
            TetGenReader.read (
               ltissue, 5000, femPath + lname + ".1.node", femPath + lname
               + ".1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
            setTissueProps(ltissue);
            myMechModel.addModel (ltissue);
            MuscleComputingProps.setTetComputingProps (ltissue);
            
            String rname = "r_"+feminfo.filename;
            FemModel3d rtissue = new FemModel3d ("r_"+feminfo.musclename);   
            TetGenReader.read (
               rtissue, 5000, femPath + rname + ".1.node", femPath + rname
               + ".1.ele", new Vector3d (GlobalParameters.scale_to_human,GlobalParameters.scale_to_human,GlobalParameters.scale_to_human));
            setTissueProps(rtissue);
            myMechModel.addModel (rtissue);
            MuscleComputingProps.setTetComputingProps (rtissue);
         }
         coupleByICP.couple (myMechModel, feminfo.musclename, feminfo.filename);
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
