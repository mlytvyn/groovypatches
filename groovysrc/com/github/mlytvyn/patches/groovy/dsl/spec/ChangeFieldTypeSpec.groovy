package com.github.mlytvyn.patches.groovy.dsl.spec

import com.github.mlytvyn.patches.groovy.context.ChangeFieldTypeContext
import de.hybris.platform.core.model.ItemModel
import de.hybris.platform.util.Config

class ChangeFieldTypeSpec {

    private final ChangeFieldTypeContext changeFieldTypeContext

    ChangeFieldTypeSpec(ChangeFieldTypeContext changeFieldTypeContext) {
        this.changeFieldTypeContext = changeFieldTypeContext
    }

    static ChangeFieldTypeContext build(
            Class<? extends ItemModel> targetClass,
            String fieldName,
            @DelegatesTo(strategy = Closure.DELEGATE_FIRST, value = ChangeFieldTypeSpec) Closure closure
    ) {
        def changeFieldTypeContext = ChangeFieldTypeContext.of(targetClass, fieldName)
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        closure.delegate = new ChangeFieldTypeSpec(changeFieldTypeContext)
        closure()
        changeFieldTypeContext
    }

    void newType(Config.DatabaseName databaseName, String fieldType) {
        changeFieldTypeContext.dbFieldType(databaseName, fieldType)
    }
}
