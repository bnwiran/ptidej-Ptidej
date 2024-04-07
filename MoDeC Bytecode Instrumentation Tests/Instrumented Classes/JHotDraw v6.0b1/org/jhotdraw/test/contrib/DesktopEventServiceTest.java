/*
 * @(#)Test.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	(c) by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.jhotdraw.test.contrib;

import java.awt.Container;

// JUnitDoclet begin import
import org.jhotdraw.contrib.Desktop;
import org.jhotdraw.contrib.DesktopEventService;
import org.jhotdraw.contrib.JPanelDesktop;
import org.jhotdraw.test.JHDTestCase;
// JUnitDoclet end import

/*
 * Generated by JUnitDoclet, a tool provided by
 * ObjectFab GmbH under LGPL.
 * Please see www.junitdoclet.org, www.gnu.org
 * and www.objectfab.de for informations about
 * the tool, the licence and the authors.
 */


// JUnitDoclet begin javadoc_class
/**
 * TestCase DesktopEventServiceTest is generated by
 * JUnitDoclet to hold the tests for DesktopEventService.
 * @see org.jhotdraw.contrib.DesktopEventService
 */
// JUnitDoclet end javadoc_class
public class DesktopEventServiceTest
// JUnitDoclet begin extends_implements
extends JHDTestCase
// JUnitDoclet end extends_implements
{
	// JUnitDoclet begin class
	// instance variables, helper methods, ... put them in this marker
	private DesktopEventService desktopeventservice;
	// JUnitDoclet end class

	/**
	 * Constructor DesktopEventServiceTest is
	 * basically calling the inherited constructor to
	 * initiate the TestCase for use by the Framework.
	 */
	public DesktopEventServiceTest(String name) {
		// JUnitDoclet begin method DesktopEventServiceTest
		super(name);
		// JUnitDoclet end method DesktopEventServiceTest
	}

	/**
	 * Factory method for instances of the class to be tested.
	 */
	public DesktopEventService createInstance() throws Exception {
		// JUnitDoclet begin method testcase.createInstance
		Desktop desktop = new JPanelDesktop(getDrawingEditor());
		return new DesktopEventService(desktop, ((Container)desktop));
		// JUnitDoclet end method testcase.createInstance
	}

	/**
	 * Method setUp is overwriting the framework method to
	 * prepare an instance of this TestCase for a single test.
	 * It's called from the JUnit framework only.
	 */
	protected void setUp() throws Exception {
		// JUnitDoclet begin method testcase.setUp
		super.setUp();
		desktopeventservice = createInstance();
		// JUnitDoclet end method testcase.setUp
	}
		
	/**
	 * Method tearDown is overwriting the framework method to
	 * clean up after each single test of this TestCase.
	 * It's called from the JUnit framework only.
	 */
	protected void tearDown() throws Exception {
		// JUnitDoclet begin method testcase.tearDown
		desktopeventservice = null;
		super.tearDown();
		// JUnitDoclet end method testcase.tearDown
	}

	// JUnitDoclet begin javadoc_method addComponent()
	/**
	 * Method testAddComponent is testing addComponent
	 * @see org.jhotdraw.contrib.DesktopEventService#addComponent(java.awt.Component)
	 */
	// JUnitDoclet end javadoc_method addComponent()
	public void testAddComponent() throws Exception {
		// JUnitDoclet begin method addComponent
		// JUnitDoclet end method addComponent
	}

	// JUnitDoclet begin javadoc_method removeComponent()
	/**
	 * Method testRemoveComponent is testing removeComponent
	 * @see org.jhotdraw.contrib.DesktopEventService#removeComponent(org.jhotdraw.framework.DrawingView)
	 */
	// JUnitDoclet end javadoc_method removeComponent()
	public void testRemoveComponent() throws Exception {
		// JUnitDoclet begin method removeComponent
		// JUnitDoclet end method removeComponent
	}

	// JUnitDoclet begin javadoc_method removeAllComponents()
	/**
	 * Method testRemoveAllComponents is testing removeAllComponents
	 * @see org.jhotdraw.contrib.DesktopEventService#removeAllComponents()
	 */
	// JUnitDoclet end javadoc_method removeAllComponents()
	public void testRemoveAllComponents() throws Exception {
		// JUnitDoclet begin method removeAllComponents
		// JUnitDoclet end method removeAllComponents
	}

	// JUnitDoclet begin javadoc_method addDesktopListener()
	/**
	 * Method testAddDesktopListener is testing addDesktopListener
	 * @see org.jhotdraw.contrib.DesktopEventService#addDesktopListener(org.jhotdraw.contrib.DesktopListener)
	 */
	// JUnitDoclet end javadoc_method addDesktopListener()
	public void testAddDesktopListener() throws Exception {
		// JUnitDoclet begin method addDesktopListener
		// JUnitDoclet end method addDesktopListener
	}

	// JUnitDoclet begin javadoc_method removeDesktopListener()
	/**
	 * Method testRemoveDesktopListener is testing removeDesktopListener
	 * @see org.jhotdraw.contrib.DesktopEventService#removeDesktopListener(org.jhotdraw.contrib.DesktopListener)
	 */
	// JUnitDoclet end javadoc_method removeDesktopListener()
	public void testRemoveDesktopListener() throws Exception {
		// JUnitDoclet begin method removeDesktopListener
		// JUnitDoclet end method removeDesktopListener
	}

	// JUnitDoclet begin javadoc_method getDrawingViews()
	/**
	 * Method testGetDrawingViews is testing getDrawingViews
	 * @see org.jhotdraw.contrib.DesktopEventService#getDrawingViews(java.awt.Component[])
	 */
	// JUnitDoclet end javadoc_method getDrawingViews()
	public void testGetDrawingViews() throws Exception {
		// JUnitDoclet begin method getDrawingViews
		// JUnitDoclet end method getDrawingViews
	}

	// JUnitDoclet begin javadoc_method getActiveDrawingView()
	/**
	 * Method testGetActiveDrawingView is testing getActiveDrawingView
	 * @see org.jhotdraw.contrib.DesktopEventService#getActiveDrawingView()
	 */
	// JUnitDoclet end javadoc_method getActiveDrawingView()
	public void testGetActiveDrawingView() throws Exception {
		// JUnitDoclet begin method getActiveDrawingView
		// JUnitDoclet end method getActiveDrawingView
	}
		
	// JUnitDoclet begin javadoc_method testVault
	/**
	 * JUnitDoclet moves marker to this method, if there is not match
	 * for them in the regenerated code and if the marker is not empty.
	 * This way, no test gets lost when regenerating after renaming.
	 * <b>Method testVault is supposed to be empty.</b>
	 */
	// JUnitDoclet end javadoc_method testVault
	public void testVault() throws Exception {
		// JUnitDoclet begin method testcase.testVault
		// JUnitDoclet end method testcase.testVault
	}
}