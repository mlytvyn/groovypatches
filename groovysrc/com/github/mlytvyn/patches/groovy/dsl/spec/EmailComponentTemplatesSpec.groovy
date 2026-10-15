package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum
import com.github.mlytvyn.patches.groovy.SiteEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class EmailComponentTemplatesSpec {

    private final PatchContextDescriber context

    EmailComponentTemplatesSpec(PatchContextDescriber context) {
        this.context = context
    }

    void all(EmailComponentTemplateEnum... emailComponentTemplates) {
        context.importEmailComponentTemplates(emailComponentTemplates)
    }

    void forSites(SiteEnum site, EmailComponentTemplateEnum... emailComponentTemplates) {
        forSites(EnumSet.of(site), emailComponentTemplates)
    }

    void forSites(Collection<SiteEnum> sites, EmailComponentTemplateEnum... emailComponentTemplates) {
        context.importEmailComponentTemplates(EnumSet.copyOf(sites), emailComponentTemplates)
    }
}
