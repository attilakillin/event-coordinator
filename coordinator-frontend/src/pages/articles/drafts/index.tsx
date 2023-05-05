import AuthenticatedFrame from "@/components/frames/authenticated-frame";
import { ArticleService } from "@/lib/services/article-service";
import { ArticleListContent } from "@/pages/articles";

/**
 * Returns the list of draft articles in a list view page.
 * Requires authentication, and redirect the user home upon
 * an unsuccessful authentication attempt.
 */
export default function ArticleDraftIndex() {
    return <>
        <AuthenticatedFrame title='Piszkozatok'>
            <ArticleListContent
                source={ArticleService.searchDrafts}
                paths={(id: number) => `/articles/drafts/${id}`}
            />
        </AuthenticatedFrame>
    </>;
};
