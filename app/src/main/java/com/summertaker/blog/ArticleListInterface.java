package com.summertaker.blog;

import com.summertaker.blog.data.Article;

public interface ArticleListInterface {

    void onTitleClick(Article article);

    void onContentClick(Article article);

    void onImageClick(Article article, String imageUrl);
}
