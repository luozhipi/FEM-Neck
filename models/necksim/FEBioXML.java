package artisynth.models.necksim;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.File;  

import maspack.matrix.Point3d;
import maspack.matrix.Vector3d;
import maspack.util.ReaderTokenizer;

import org.dom4j.Attribute;  
import org.dom4j.Document;  
import org.dom4j.DocumentException;  
import org.dom4j.Element;  
import org.dom4j.ProcessingInstruction;  
import org.dom4j.VisitorSupport;  
import org.dom4j.io.SAXReader;  

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;  
import java.io.IOException;  
import org.dom4j.DocumentHelper;  
import org.dom4j.io.OutputFormat;  
import org.dom4j.io.XMLWriter; 

import artisynth.core.femmodels.FemElement3d;
import artisynth.core.femmodels.FemElement3dList;
import artisynth.core.femmodels.FemModel3d;
import artisynth.core.femmodels.FemNode3d;
import artisynth.core.femmodels.TetElement;
import artisynth.core.mechmodels.PointList;

public class FEBioXML
{
   public static ArrayList<Integer> read(String xmlname, int bcIdx) throws DocumentException
   {
      ArrayList<Integer> bcs = new ArrayList<Integer>();
      SAXReader reader = new SAXReader();  
      Document document = reader.read(new File(xmlname));   
      Element root = document.getRootElement();
      Element Boundary = root.element("Boundary");
      List fixnodes = Boundary.elements("fix"); 
      int counter = 1;
      for (Iterator it = fixnodes.iterator(); it.hasNext();) { 
         Element fixelm = (Element) it.next();  
         if(counter == bcIdx)
         {
            List nodes = fixelm.elements("node"); 
            for (Iterator it01 = nodes.iterator(); it01.hasNext();) {  
               Element nodeelm = (Element) it01.next();  
               Attribute attribute=nodeelm.attribute("id");
               String text=attribute.getText();
               bcs.add (Integer.parseInt (text));
            }
         }
         counter++;
     }
      return bcs;
   }
   public static void write(String xmlname, FileReader nodeReader, FileReader elemFile)
   {
      try {  
         OutputFormat   format  = new OutputFormat("  ",true);
         //format.setLineSeparator("\n");
         format.setEncoding("ISO-8859-1");
         XMLWriter writer = new XMLWriter(new FileWriter(xmlname), format);  
         Document doc = createDoc(nodeReader,elemFile);  
         writer.write(doc);  
         writer.close();  

     } catch (IOException e) {  
         // TODO Auto-generated catch block  
         e.printStackTrace();  
     }  
   }
   public static Document createDoc(FileReader nodeReader, FileReader elemReader) throws IOException {  
      Document doc = DocumentHelper.createDocument();  
      Element root = doc.addElement("febio_spec");  
      root.addAttribute ("version","1.2");
      Element Globals = root.addElement("Globals");
      Element Constants = Globals.addElement("Constants");
      Element T = Constants.addElement("T").addText("0");
      Element R = Constants.addElement("R").addText("0");
      Element Fc = Constants.addElement("Fc").addText("0");     
      Element Geometry = root.addElement("Geometry");
      Element Nodes = Geometry.addElement("Nodes");   
      ReaderTokenizer nodeFile =
      new ReaderTokenizer (new BufferedReader (nodeReader));
      nodeFile.nextToken();
      nodeFile.nextToken();
      nodeFile.nextToken();
      nodeFile.nextToken();
      while (nodeFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!nodeFile.tokenIsInteger()) {
         }
         int index = (int)nodeFile.lval;
         int id = (index+1);
         Point3d coords = new Point3d();
         for (int i = 0; i < 3; i++) {
            coords.set (i, nodeFile.scanNumber());
         }
         String txt = coords.x+","+coords.y+","+coords.z;
         Nodes.addElement ("node").addAttribute("id",Integer.toString(id)).addText(txt);
      }
      nodeReader.close ();
      Element Elements = Geometry.addElement("Elements");
      ReaderTokenizer elemFile =
      new ReaderTokenizer (new BufferedReader (elemReader));

      elemFile.nextToken();
      elemFile.nextToken();
      elemFile.nextToken();
      while (elemFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!elemFile.tokenIsInteger()) {

         }
         int index = (int)elemFile.lval;

         int[] idxs = new int[8];
         for (int i = 0; i < 8; i++) {
            idxs[i] = elemFile.scanInteger();
         }
         int  id1 = idxs[0]+1;
         int   id2 = idxs[1]+1;
         int id3 = idxs[2]+1;
         int id4 = idxs[3]+1;
         int id5 = idxs[4]+1;
         int id6 = idxs[5]+1;
         int id7 = idxs[6]+1;
         int id8 = idxs[7]+1;
         int id = index+1;
         String txt = id1 +",   "+id2+",    "+id3+",    "+id4+",    "+id5+",    "+id6+",    "+id7+",    "+id8;
         Elements.addElement ("hex8").addAttribute("id",Integer.toString(id)).addAttribute("mat","0").addText(txt);
      }
      elemReader.close ();
      Element Output = root.addElement("Output");
      Element plotfile = Output.addElement("plotfile").addAttribute("type","febio");
      plotfile.addElement("var").addAttribute("type","displacement");
      plotfile.addElement("var").addAttribute("type","effective fluid pressure");
      plotfile.addElement("var").addAttribute("type","fluid flux");
      plotfile.addElement("var").addAttribute("type","relative volume");
      plotfile.addElement("var").addAttribute("type","stress");
      return doc;  
  }  
   
   public static void writeTetgen(String xmlname, FileReader nodeReader, FileReader elemFile)
   {
      try {  
         OutputFormat   format  = new OutputFormat("  ",true);
         //format.setLineSeparator("\n");
         format.setEncoding("ISO-8859-1");
         XMLWriter writer = new XMLWriter(new FileWriter(xmlname), format);  
         Document doc = createDocTetgen(nodeReader,elemFile);  
         writer.write(doc);  
         writer.close();  

     } catch (IOException e) {  
         // TODO Auto-generated catch block  
         e.printStackTrace();  
     }  
   }
   public static Document createDocTetgen(FileReader nodeReader, FileReader elemReader) throws IOException {  
      Document doc = DocumentHelper.createDocument();  
      Element root = doc.addElement("febio_spec");  
      root.addAttribute ("version","1.2");
      Element Globals = root.addElement("Globals");
      Element Constants = Globals.addElement("Constants");
      Element T = Constants.addElement("T").addText("0");
      Element R = Constants.addElement("R").addText("0");
      Element Fc = Constants.addElement("Fc").addText("0");     
      Element Geometry = root.addElement("Geometry");
      Element Nodes = Geometry.addElement("Nodes");   
      ReaderTokenizer nodeFile =
      new ReaderTokenizer (new BufferedReader (nodeReader));
      nodeFile.nextToken();
      nodeFile.nextToken();
      nodeFile.nextToken();
      nodeFile.nextToken();
      while (nodeFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!nodeFile.tokenIsInteger()) {
         }
         int index = (int)nodeFile.lval;
         int id = (index+1);
         Point3d coords = new Point3d();
         for (int i = 0; i < 3; i++) {
            coords.set (i, nodeFile.scanNumber());
         }
         String txt = coords.x+","+coords.y+","+coords.z;
         Nodes.addElement ("node").addAttribute("id",Integer.toString(id)).addText(txt);
      }
      nodeReader.close ();
      Element Elements = Geometry.addElement("Elements");
      ReaderTokenizer elemFile =
      new ReaderTokenizer (new BufferedReader (elemReader));

      elemFile.nextToken();
      elemFile.nextToken();
      elemFile.nextToken();
      while (elemFile.nextToken() != ReaderTokenizer.TT_EOF) {
         if (!elemFile.tokenIsInteger()) {

         }
         int index = (int)elemFile.lval;

         int[] idxs = new int[4];
         for (int i = 0; i < 4; i++) {
            idxs[i] = elemFile.scanInteger();
         }
         int  id1 = idxs[0]+1;
         int   id2 = idxs[1]+1;
         int id3 = idxs[2]+1;
         int id4 = idxs[3]+1;
         int id = index+1;
         String txt = id1 +",   "+id2+",    "+id3+",    "+id4;
         Elements.addElement ("tet4").addAttribute("id",Integer.toString(id)).addAttribute("mat","0").addText(txt);
      }
      elemReader.close ();
      Element Output = root.addElement("Output");
      Element plotfile = Output.addElement("plotfile").addAttribute("type","febio");
      plotfile.addElement("var").addAttribute("type","displacement");
      plotfile.addElement("var").addAttribute("type","effective fluid pressure");
      plotfile.addElement("var").addAttribute("type","fluid flux");
      plotfile.addElement("var").addAttribute("type","relative volume");
      plotfile.addElement("var").addAttribute("type","stress");
      return doc;  
  } 
}