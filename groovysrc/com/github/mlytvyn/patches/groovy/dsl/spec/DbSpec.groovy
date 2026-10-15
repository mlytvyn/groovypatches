package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext
import com.github.mlytvyn.patches.groovy.context.DropColumnContext
import com.github.mlytvyn.patches.groovy.context.patch.PatchContextDescriber
import de.hybris.platform.core.model.ItemModel

class DbSpec {

    private final PatchContextDescriber context

    DbSpec(PatchContextDescriber context) {
        this.context = context
    }

    void changeFieldType(ChangeFieldTypeContext... changeFieldTypeContexts) {
        context.changeFieldType(changeFieldTypeContexts)
    }

    void changeFieldType(
            Class<? extends ItemModel> targetClass,
            String fieldName,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ChangeFieldTypeSpec) Closure closure
    ) {
        context.changeFieldType(ChangeFieldTypeSpec.build(targetClass, fieldName, closure))
    }

    void dropColumn(DropColumnContext... dropColumnContexts) { context.dropColumn(dropColumnContexts) }

    void dropColumn(Class<? extends ItemModel> targetClass, String columnName) {
        context.dropColumn(DropColumnContext.of(targetClass, columnName))
    }
}
