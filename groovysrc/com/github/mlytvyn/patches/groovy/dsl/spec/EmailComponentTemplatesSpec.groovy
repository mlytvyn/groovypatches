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
            Object... siteArgsThenClosure
    ) {
        final Closure closure = siteArgsThenClosure[-1] as Closure
        final List<Object> siteArgs = siteArgsThenClosure[0..-2]
        final Collection<SiteEnum> sites = (siteArgs.size() == 1 && siteArgs[0] instanceof Collection)
                ? siteArgs[0] as Collection<SiteEnum>
                : siteArgs as Collection<SiteEnum>

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
