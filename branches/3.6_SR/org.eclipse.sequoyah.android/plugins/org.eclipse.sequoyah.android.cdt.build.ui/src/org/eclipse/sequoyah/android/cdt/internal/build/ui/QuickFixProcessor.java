/**
 * @author: Carlos Alberto Souto Junior
 */

package org.eclipse.sequoyah.android.cdt.internal.build.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.ui.text.java.IInvocationContext;
import org.eclipse.jdt.ui.text.java.IJavaCompletionProposal;
import org.eclipse.jdt.ui.text.java.IProblemLocation;
import org.eclipse.jdt.ui.text.java.IQuickFixProcessor;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;

public class QuickFixProcessor implements IQuickFixProcessor
{

    public boolean hasCorrections(ICompilationUnit unit, int problemId)
    {
        // TODO Auto-generated method stub
        return true;
    }

    public IJavaCompletionProposal[] getCorrections(IInvocationContext context,
            IProblemLocation[] locations) throws CoreException
    {
        IJavaCompletionProposal proposal =
                new NDKCompletionProposal((IQuickAssistInvocationContext) context);
        IJavaCompletionProposal[] proposals = new IJavaCompletionProposal[1];
        proposals[0] = proposal;
        return proposals;

    }
}
