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

    void forSites(
            SiteEnum site,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        forSites(EnumSet.of(site), closure)
    }

    void forSites(
            Collection<SiteEnum> sites,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesListSpec) Closure closure
    ) {
        def spec = new EmailComponentTemplatesListSpec()
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = spec
        closure()
        context.importEmailComponentTemplates(EnumSet.copyOf(sites), spec.emailComponentTemplates() as EmailComponentTemplateEnum[])
    }
}
