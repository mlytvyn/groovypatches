package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.EmailComponentTemplateEnum

class EmailComponentTemplatesListSpec {

    private final List<EmailComponentTemplateEnum> emailComponentTemplates = []

    void template(EmailComponentTemplateEnum emailComponentTemplate) {
        emailComponentTemplates.add(emailComponentTemplate)
    }

    List<EmailComponentTemplateEnum> emailComponentTemplates() { emailComponentTemplates }
}
