package de.plushnikov.intellij.plugin.processor.clazz;

import de.plushnikov.intellij.plugin.problem.ProblemBuilder;
import de.plushnikov.intellij.plugin.processor.LombokPsiElementUsage;
import de.plushnikov.intellij.plugin.psi.LombokLightMethodBuilder;
import de.plushnikov.intellij.plugin.util.PsiClassUtil;
import de.plushnikov.intellij.plugin.util.PsiMethodUtil;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.sictiy.processor.single.SingleInstance;

/**
 * @author sictiy.xu
 * @version 2019/10/14 15:31
 **/
public class SingleInstanceProcessor extends AbstractClassProcessor
{
  private static final String METHOD_NAME = "getInstance";

  protected SingleInstanceProcessor()
  {
    super(PsiMethod.class, SingleInstance.class);
  }

  @Override
  protected boolean validate(@NotNull PsiAnnotation psiAnnotation, @NotNull PsiClass psiClass, @NotNull ProblemBuilder builder)
  {
    if (!validateAnnotationOnRigthType(psiClass, builder))
    {
      return false;
    }
    if (!validateExistingMethods(psiClass, builder))
    {
      return false;
    }
    return true;
  }

  private boolean validateAnnotationOnRigthType(@NotNull PsiClass psiClass, @NotNull ProblemBuilder builder)
  {
    boolean result = true;
    if (psiClass.isAnnotationType() || psiClass.isInterface())
    {
      builder.addError("@SingleInstance is only supported on a class or enum type");
      result = false;
    }
    return result;
  }

  private boolean validateExistingMethods(@NotNull PsiClass psiClass, @NotNull ProblemBuilder builder)
  {
    boolean result = true;

    final Collection<PsiMethod> classMethods = PsiClassUtil.collectClassMethodsIntern(psiClass);
    if (PsiMethodUtil.hasMethodByName(classMethods, METHOD_NAME))
    {
      builder.addWarning("Not generated '%s'(): A method with same name already exists", METHOD_NAME);
      result = false;
    }

    return result;
  }

  @Override
  protected void generatePsiElements(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation, @NotNull List<? super PsiElement> target)
  {
    target.addAll(createGetInstanceMethod(psiClass, psiAnnotation));
  }

  private Collection<PsiMethod> createGetInstanceMethod(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation)
  {
    final PsiMethod method = createOneGetInstanceMethod(psiClass, psiAnnotation);
    return Collections.singletonList(method);
  }

  private PsiMethod createOneGetInstanceMethod(@NotNull PsiClass psiClass, @NotNull PsiAnnotation psiAnnotation)
  {
    final PsiManager psiManager = psiClass.getManager();

    final LombokLightMethodBuilder methodBuilder = new LombokLightMethodBuilder(psiManager, METHOD_NAME);
    methodBuilder.withMethodReturnType(PsiClassUtil.getTypeWithGenerics(psiClass));
    methodBuilder.withContainingClass(psiClass);
    methodBuilder.withNavigationElement(psiAnnotation);
    methodBuilder.withModifier(PsiModifier.PUBLIC);
    methodBuilder.withModifier(PsiModifier.STATIC);
    return methodBuilder;
  }

  @Override
  public LombokPsiElementUsage checkFieldUsage(@NotNull PsiField psiField, @NotNull PsiAnnotation psiAnnotation)
  {
    return LombokPsiElementUsage.NONE;
  }
}
