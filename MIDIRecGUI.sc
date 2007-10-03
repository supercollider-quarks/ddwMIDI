
MIDIRecGUI : HJHObjectGui {
	var	<mainView,		// holds container for all views in this object
//		seqDrag,		// drag source for sequence names
		<seqMenu,
		<nameSet,
		<statusButton,
//		<translatorMenu,	// deprecated
//		<editTranslator,
		<quant;		// record-quantize control

	guiBody { arg lay;
		layout = lay;
		mainView.isNil.if({	// if this gui is already open, don't recreate
			mainView = FixedWidthFlowView(layout, argBounds ?? { Rect(0, 0, 700, 300) });

//			seqDrag = SCDragSource(mainView, Rect(0, 0, 60, 20))
//				.silentObject_(model.current)
//				.align_(\center)
//				.string_("[[drag me]]");
//
			seqMenu = GUI.popUpMenu.new(mainView, Rect(0, 0, 120, 20))
				.items_(model.menuItems)  //.align(\center);
				.action_({ arg m;
					model.recorder.isNil.if({	// not recording, so...
						model.value_(m.value)	// update current buffer
					});	// otherwise, ignore (can't change buffer while recording)
				});

			nameSet = ToggleTextField(mainView, Rect(0, 0, 120, 20))
				.action_({ arg t;
					model.current.name_(t.string);	// set the name
					this.refreshMenu;				// fix the menu
					{ t.string_(""); nil }.defer;	// reset string to nothing
				});

			statusButton = GUI.button.new(mainView, Rect(0, 0, 80, 20)).states_([
				["idle", Color.new255(12, 9, 96), Color.new255(255, 178, 203)],
				["RECORD", Color.new255(12, 9, 96), Color.new255(255, 39, 19)]
			])	.value_(0)
				.action_({ |b|
					(b.value > 0).if({
						model.initRecord;
					}, {
						model.stopRecord;
					});
				});
			
//			translatorMenu = SCPopUpMenu(mainView, Rect(0, 0, 120, 20))
//				.items_(Library.at(\bufTrans).keys.asArray.sort);
//			
//			editTranslator = ActionButton(mainView, "edit", {
//				var d;
//				d = Document("Edit translator parameters",
//					Library.at(\bufTrans, this.currentTranslator).asEditString)
//					.syntaxColorize;
//				d.onClose_({
//					d.string.interpret;	// this should put a new version in the library
//					this.refreshMenu;	// so you can create a new xlator by editing an old one
//									// and changing the name
//				});
//			}, minWidth:70);

			// implement quantize later -- actually, apply quantize at translation time
			model.view = this;		// so it can find me
			this.refresh(model);
			masterLayout.recursiveResize;
		});
	}
	
	refresh { arg changer;
//		(changer.class == MIDIBufManager).if({
//"MIDIRecGUI-refresh: MIDIBufManager (updating value)".postln;
			model.current.isNil.if({		// pointing to empty space at end?
				{ // seqDrag.silentObject_(nil); 
				  seqMenu.value_(model.bufs.size);
				  nil }.defer;
			}, {
				{ // seqDrag.silentObject_(model.current);
				  seqMenu.value_(model.value);
				  nil }.defer;
			});
//			^this
//		});
//		(changer.class == MIDIRecControl).if({
//"MIDIRecGUI-refresh: MIDIRecControl (updating items)".postln;
			{ statusButton.value_(model.recorder.notNil.binaryValue); 
			  seqMenu.items_(model.menuItems);
			  nil }.defer;
//			^this
//		});
		
	}
	
//	currentTranslator {	// returns symbolic name of current choice in translatorMenu
//		^translatorMenu.items.wrapAt(translatorMenu.value).asSymbol
//	}
	
	refreshMenu {
		{
			seqMenu.items_(model.menuItems);
//			translatorMenu.items_(Library.at(\bufTrans).keys.asArray.sort);
		}.defer;
	}
	
	setName {		// give focus to nameSet so user can type name
		{ nameSet.focus(true); }.defer;
	}

//	remove {}		// ?????
	
}
