package de.plushnikov.intellij.plugin.action.delombok;

import de.plushnikov.intellij.plugin.processor.clazz.SingleInstanceProcessor;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.components.ServiceManager;

public class DelombokSingleInstanceAction extends AbstractDelombokAction {
  @NotNull
  protected DelombokHandler createHandler() {
    return new DelombokHandler(ServiceManager.getService(SingleInstanceProcessor.class));
  }
}
