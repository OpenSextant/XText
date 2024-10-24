package org.opensextant.xtext.collectors.web;

public interface CrawlFilter {

    /**
     * @return the allowCurrentDirOnly
     */
    boolean isAllowCurrentDirOnly();

    /**
     * If crawl requested URL A, then only URLS starting with string(A) will be accepted.
     *
     * @param allowCurrentDirOnly the allowCurrentDirOnly to set
     */
    void setAllowCurrentDirOnly(boolean allowCurrentDirOnly);

    /**
     * Allow all links to be archived when link from requested page resides on current web site;  Even when requested crawl is deeper than top level site home page.
     * For example, requested http://a.com/b/c, any link on a.com will be grabbed.
     *
     * @return the allowCurrentSiteOnly
     */
    boolean isAllowCurrentSiteOnly();

    /**
     * @param allowCurrentSiteOnly the allowCurrentSiteOnly to set
     */
    void setAllowCurrentSiteOnly(boolean allowCurrentSiteOnly);

}
