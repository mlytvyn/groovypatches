package com.github.mlytvyn.patches.groovy.jalo;

import com.github.mlytvyn.patches.groovy.constants.GroovypatchesConstants;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.extension.ExtensionManager;
import org.apache.log4j.Logger;

public class GroovypatchesManager extends GeneratedGroovypatchesManager
{
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger( GroovypatchesManager.class.getName() );
	
	public static final GroovypatchesManager getInstance()
	{
		ExtensionManager em = JaloSession.getCurrentSession().getExtensionManager();
		return (GroovypatchesManager) em.getExtension(GroovypatchesConstants.EXTENSIONNAME);
	}
	
}
