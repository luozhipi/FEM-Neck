package artisynth.models.necksim;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import maspack.geometry.PolygonalMesh;
import maspack.render.RenderProps;
import maspack.util.ReaderTokenizer;
import artisynth.core.mechmodels.MechModel;
import artisynth.core.mechmodels.RigidBody;
import artisynth.core.util.ArtisynthPath;

public class NeckSkeleton{
   
   public MechModel myMechModel;
   protected ArrayList<BodyInfo> bodyInfoList = new ArrayList<BodyInfo>();
   private String bodyListFilename = "bodylist.txt";
   private class BodyInfo {
      public String name;
      public String meshName;
      public void scan(ReaderTokenizer rtok) throws IOException {
         name = rtok.sval;
         rtok.nextToken();
         meshName = rtok.sval;
      }
   }
   public NeckSkeleton(MechModel mechModel) throws Exception
   {
      this.myMechModel = mechModel;
      loadRigidObj();
      BodyPos.setPos (myMechModel);
      BodyDynamics.setDampingProp(myMechModel);
      SpineJoints.createJoints (myMechModel);
      CricoThyroidJoint.createJoint (myMechModel);
      JawDeepMuscleReader jawmusclereader = new JawDeepMuscleReader(myMechModel);
      jawmusclereader.assemble ();
   }
   public void loadRigidObj() throws Exception
   {
      bodyInfoList = readBodyInfoList(bodyListFilename);
      for (BodyInfo bodyInfo : bodyInfoList) {
         RigidBody body;
         body = new RigidBody();
         body.setName(bodyInfo.name);
         String meshFilename = ArtisynthPath.getSrcRelativePath(NeckSkeleton.class,
            "rigid/" + bodyInfo.meshName);
         PolygonalMesh mesh = new PolygonalMesh();
         try {
            mesh.read(new BufferedReader(new FileReader(meshFilename)));
         } catch (IOException e) {
            e.printStackTrace();
            return;
         }
         mesh.scale (GlobalParameters.scale_to_human);
         mesh.triangulate();
         body.setMesh(mesh, meshFilename);
         myMechModel.addRigidBody(body);
         RenderProps.setShading(body, RenderProps.Shading.GOURARD);
         RenderProps.setFaceColor(body, Color.white);
         //RenderProps.setFaceColor(body, new Color(1f, 0.8f, 0.6f));
         RenderProps.setShininess (body, 1);
         RenderProps.setFaceStyle(body, RenderProps.Faces.FRONT_AND_BACK);
         RenderProps.setVisible (body, GlobalParameters.showBone);
      }
   }
   private ArrayList<BodyInfo> readBodyInfoList(String filename)
   throws IOException {
      ReaderTokenizer rtok = new ReaderTokenizer(new FileReader(ArtisynthPath
         .getSrcRelativePath(NeckSkeleton.class, "rigid/" + filename)));
      rtok.wordChars(".");
      ArrayList<BodyInfo> bodyInfoList = new ArrayList<BodyInfo>();
      while (rtok.nextToken() != ReaderTokenizer.TT_EOF) {
         BodyInfo bi = new BodyInfo();
         bi.scan(rtok);
         bodyInfoList.add(bi);
      }
      return bodyInfoList;
   }

}
