

package com.github.mlytvyn.patches.groovy.context.global.actions;

import com.github.mlytvyn.patches.groovy.context.global.GlobalContext;
import de.hybris.platform.core.initialization.SystemSetupContext;

public interface GlobalContextAction<T extends GlobalContext> {

    void execute(SystemSetupContext context, T globalContext);
}
