package controllers

import play._
import play.mvc._

object Application extends Controller {
  import models._
  import views.Application._

  def index = {
    val allPosts = Post.allWithAuthorAndComments
    html.index(
      front = allPosts.headOption, 
      older = allPosts.drop(1)
    )
  }
}
