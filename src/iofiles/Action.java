package iofiles;

import platformpages.PlatformPage;

public final class Action {
    private String type;
    private String page;
    private Credentials credentials;
    private String feature;
    private final String startsWith;
    private final Filters filters;
    private final String count;
    private String movie;
    private final int rate;
    private final String subscribedGenre;
    private final String deletedMovie;
    private final Movie addedMovie;
    private PlatformPage backPage;

    public Action() {
        type = null;
        page = null;
        credentials = null;
        feature = null;
        startsWith = null;
        filters = null;
        count = null;
        movie = null;
        rate = 0;
        subscribedGenre = null;
        deletedMovie = null;
        addedMovie = null;
        backPage = null;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getPage() {
        return page;
    }

    public void setPage(final String page) {
        this.page = page;
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public void setCredentials(final Credentials credentials) {
        this.credentials = credentials;
    }

    public String getFeature() {
        return feature;
    }

    public void setFeature(final String feature) {
        this.feature = feature;
    }

    public String getStartsWith() {
        return startsWith;
    }


    public Filters getFilters() {
        return filters;
    }

    public String getCount() {
        return count;
    }

    public String getMovie() {
        return movie;
    }

    public void setMovie(final String movie) {
        this.movie = movie;
    }

    public int getRate() {
        return rate;
    }

    public String getSubscribedGenre() {
        return subscribedGenre;
    }

    public String getDeletedMovie() {
        return deletedMovie;
    }

    public Movie getAddedMovie() {
        return addedMovie;
    }

    public PlatformPage getBackPage() {
        return backPage;
    }

    public void setBackPage(final PlatformPage backPage) {
        this.backPage = backPage;
    }
}
