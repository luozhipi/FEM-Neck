'''
    blenshape animation
    By initial by Felix Schlitter 2012
	modified by Zhiping 2013
'''

import os.path
import pymel.core as pm
import maya.cmds as cmds

class Import_objSequence_manager():
    ''' Manage ui and actions '''
    def __init__(self):
        ''' Build the ui '''
        self.win = pm.windows.window(title = "Import Obj Sequence", w = 300, h = 300, rtf = True, ret = True, s = False)
        
        # Filebrowser
        pm.windows.columnLayout()
        pm.windows.rowLayout(nc = 2)
        self.pathui = pm.windows.textField(w = 300, changeCommand = lambda x: self.updateList(), text = os.path.abspath(''))
        pm.windows.button(label = 'Browse...', command = lambda x: self.promptFolderDialog(), w = 100)
        
        # Obj List
        pm.setParent( '..' )
        pm.windows.columnLayout()
        pm.windows.rowLayout(nc = 1)
        self.listui = pm.windows.textScrollList(w = 400, h = 200, ams = True)
        
        # Main Buttons
        pm.setParent( '..' )
        pm.windows.columnLayout()
        pm.windows.button(label = 'Refresh list', command = lambda x: self.updateList(), w = 400)
        pm.windows.button(label = 'Import obj sequence', command = lambda x: self.import_objSeq(), w = 400)
       
        # Options
        pm.setParent( '..' )
        pm.windows.frameLayout(cll = True, l = 'Options', w = 400, mw = 10, mh = 5)
        pm.windows.columnLayout()
        pm.windows.rowLayout(nc = 3)
        pm.windows.text(label = 'Speed modifier:')
        self.speedmod = pm.windows.intField(v = 1, w = 40, min = 1)
        pm.windows.text(label = '(Frames per each obj)')
        pm.setParent( '..' )
        pm.windows.columnLayout()
        self.bakeAndDelete = pm.windows.checkBox(l = 'Bake and delete blendshapes')
        self.reverse = pm.windows.checkBox(l = 'Reverse')
        self.setRange = pm.windows.checkBox(l = 'Set frame range to animation', v = True)

        self.updateList()
    
    def promptFolderDialog(self):
        ''' Display a folder prompt and update corresponding field '''
        dir = pm.windows.promptForFolder()
        if dir:
            self.pathui.setText(dir)
            self.updateList()
    
    def getDir(self):
        ''' Get the cleaned path name from the ui ''' 
        return os.path.abspath(self.pathui.getText())
    
    def getObjList(self):
        ''' Get the list of physically present obj files in this directory '''
        dir = self.getDir()
        print (dir)
        obj_names= []
        for i in range(0, 326):
            obj_names.append('C:/work/headnecksim/artisynth_2.8/src/artisynth/models/necksim/result/result_' + str(i) +".obj")
        #return pm.system.getFileList(folder = (dir + '/'), filespec = '*.obj')
        return obj_names
    
    def updateList(self):
        ''' Updates the scroll list to reflect the new directory '''
        objs = self.getObjList()
        self.listui.removeAll()
        if not objs:
            self.listui.append('No OBJs found in this directory!')
            return
        self.listui.append(objs)
        # Workaround to select all items. 'selectAll' is broken in pyMel 1.0.0
        for index, obj in enumerate(objs):
            cmds.textScrollList(self.listui, edit = True, selectIndexedItem = (index + 1))

    def import_obj(self, objname):
        ''' Import a single obj file and return the shape nodes created '''
        dir = self.getDir()
        #print ("importing " + objname)
        imported_nodes = cmds.file(os.path.join(dir, objname), i = True, type = 'OBJ', rnn = True, ra = True, namespace = 'objseq', options = 'mo = 1',  pr = True, loadReferenceDepth = 'all')
        imported_shapes = pm.general.ls(imported_nodes, type = 'shape')
        #cmds.polyTriangulate(imported_shapes)
        #cmds.polySmooth( imported_shapes)
        return imported_shapes

    def import_objSeq(self):
        ''' Import an obj sequence '''
        dir = self.getDir()
        
        # Get the list of physically present objs
        obj_names = self.getObjList()
        
        # Get the selected objs from the interface
        sel_objs = self.listui.getSelectIndexedItem()
        
        # We want at least 2 objs
        if ((len(obj_names) < 2) or (len(sel_objs) < 2)):
            print ("Need at least 2 objs to make an animation!")
            return
                
        # Reverse lists if required
        if self.reverse.getValue():
            obj_names = obj_names[::-1]
            sel_objs = sel_objs[::-1]
            
        # Speed mod
        speedmod = self.speedmod.getValue()
        
        # Import the rest
        startframe = 1
        frame = startframe
        pm.animation.currentTime(frame)

        # Filled on first found obj
        orig_objs = []
        
        # Create list of objs to process
        real_objs = []
        for i, obj_name in enumerate(obj_names):
            i += 1
            if (i in sel_objs): 
                real_objs.append(obj_name)
        
        # Import first one
        # Each obj file can consist of several meshes. We store a list of all meshes from the import
        # and expect all following .obj files to have the same number of objects, specified in the
        # same order.
        orig_objs = self.import_obj(real_objs[0])
        real_objs = real_objs[1:]
        helper_objs = []
        # Process the rest elements
        foundFirst = False
        for obj_name in real_objs:
            
            # Import current obj
            cur_objs = self.import_obj(obj_name)
            helper_objs += cur_objs
            
            # Ensure right length
            if (len(cur_objs) != len(orig_objs)):
                pm.general.delete(cur_objs)
                continue
            
            # process each shape
            for index, cur_obj in enumerate(cur_objs):
                source = cur_obj
                target = orig_objs[index]
                
                # Apply the blendshape deformer
                pm.general.select(source)
                pm.general.select(target, add = True)
                # Try to make a blendshape
                try:
                    blendshape = pm.animation.blendShape()[0]
                    helper_objs.append(blendshape)
                except:
                    pm.general.delete(cur_objs)
                    break
                
                # Animate the blendshape
                pm.animation.setKeyframe(blendshape + ".weight[0]", value = 0, time = frame)
                pm.animation.setKeyframe(blendshape + ".weight[0]", value = 1, time = (frame + speedmod))
                
            # Next frame
            frame += speedmod
        
        # Select the first obj
        pm.general.select(orig_objs)
        
        # Set playback range if requested
        if (self.setRange.getValue()):
            pm.animation.playbackOptions(min = 1,
                                         max = frame,
                                         ast = 1
                                         )
        
        # Bake and delete if required
        if self.bakeAndDelete.getValue():
            pm.animation.bakeResults(orig_objs, sm = True, t = (str(startframe) + ':' + str(frame)), dic = True, pok = True, sac = False, ral = False, bol = False, cp = True, s = False)
            for helper_obj in helper_objs:
                try:
                    pm.general.delete(helper_obj)
                except:
                    continue
            
            
    def show(self):
        ''' Display the ui '''
        self.win.show()

# By importing the module, a manager instance is launched that will keep track of settings and so on
mngr = Import_objSequence_manager()

def show():
    ''' Main entry point for usage in Maya '''
    mngr.show()