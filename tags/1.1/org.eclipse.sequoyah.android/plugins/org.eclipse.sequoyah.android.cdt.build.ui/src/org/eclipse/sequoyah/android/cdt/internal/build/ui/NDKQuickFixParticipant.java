/**
 * @author: Carlos Alberto Souto Junior
 */
package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CompilationParticipant;
import org.eclipse.jdt.core.compiler.ReconcileContext;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class NDKQuickFixParticipant extends CompilationParticipant
{

    private NDKNativeMethodDetectionVisitor visitor = new NDKNativeMethodDetectionVisitor();

    private CompilationUnit compilationUnit;

    private String[] projectNature;

    /**
     * 
     */
    public NDKQuickFixParticipant()
    {
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean isActive(IJavaProject project)
    {

        try
        {
            projectNature = project.getProject().getDescription().getNatureIds();

        }
        catch (CoreException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return true;

    }

    @Override
    public void reconcile(ReconcileContext context)
    {

        try
        {
            Boolean isAndroid = true;
            visitor.resetVisitor();
            compilationUnit = context.getAST3();
            compilationUnit.accept(visitor);

            if (visitor.isNativePresence())
            {

                for (String s : projectNature)
                {
                    isAndroid =
                            isAndroid
                                    && (!(s.equals("org.eclipse.cdt.core.cnature") || s
                                            .equals("org.eclipse.cdt.core.ccnature")));
                }

                if (isAndroid
                        && (Platform.getBundle("org.eclipse.sequoyah.android.cdt.build.core") != null))
                {
                    NDKNativeProblem[] problems;
                    problems = visitor.getProblems();
                    context.putProblems("org.eclipse.sequoyah.android.cdt.build.ui.managedMarker",
                            problems);
                }

            }

        }
        catch (JavaModelException e)
        {

            // TODO Auto-generated catch block

        }

    }

}
