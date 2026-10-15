package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum
import com.github.mlytvyn.patches.groovy.SiteEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class EmailComponentTemplatesSpec {

    private final PatchContextDescriber context

    EmailComponentTemplatesSpec(PatchContextDescriber context) {
        this.context = context
    }

    void allSites(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        context.importEmailComponentTemplates(collectTemplates(closure))
    }

    void forSites(
            SiteEnum site,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        forSites([site], closure)
    }

    void forSites(
            SiteEnum site1, SiteEnum site2,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        forSites([site1, site2], closure)
    }

    void forSites(
            SiteEnum site1, SiteEnum site2, SiteEnum site3,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        forSites([site1, site2, site3], closure)
    }

    void forSites(
            SiteEnum site1, SiteEnum site2, SiteEnum site3, SiteEnum site4,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        forSites([site1, site2, site3, site4], closure)
    }

    void forSites(
            Collection<SiteEnum> sites,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        context.importEmailComponentTemplates(EnumSet.copyOf(sites), collectTemplates(closure))
    }

    private EmailComponentTemplateEnum[] collectTemplates(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        def spec = new EmailComponentTemplatesListSpec()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = spec
        closure()
        spec.emailComponentTemplates() as EmailComponentTemplateEnum[]
    }
}
