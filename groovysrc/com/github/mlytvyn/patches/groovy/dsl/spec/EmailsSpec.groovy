package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.EmailTemplateEnum
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber

class EmailsSpec {

    private final PatchContextDescriber context

    EmailsSpec(PatchContextDescriber context) {
        this.context = context
    }

    void templates(EmailTemplateEnum... emailTemplates) { context.importEmailTemplates(emailTemplates) }

    void componentTemplates(
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = EmailComponentTemplatesSpec) Closure closure
    ) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new EmailComponentTemplatesSpec(context)
        closure()
    }
}
