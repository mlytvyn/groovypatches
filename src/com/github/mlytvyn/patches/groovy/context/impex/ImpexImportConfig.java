package com.github.mlytvyn.patches.groovy.context.impex;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Builder
@Accessors(chain = true, fluent = true)
public class ImpexImportConfig implements Serializable {

    private static final long serialVersionUID = -609475195699174983L;
    private boolean legacyMode;
    private boolean failOnError;
    private boolean errorIfMissing;
    private boolean removeOnSuccess;
    private boolean synchronous;
    @Builder.Default
    private boolean enableCodeExecution = true;

}
