
package models

import play.db.anorm._
import play.db.anorm.SqlParser._

case class User(
    id: Pk[Long], 
    email: String, password: String, fullname: String, isAdmin: Boolean
)

import play.db.anorm.defaults._

object User extends Magic[User]{
    def connect(email: String, password: String) = {
        User.find("email = {email} and password = {password}")
            .on("email" -> email, "password" -> password)
            .first()
    }
}

import java.util.{Date}

case class Post(
    id: Pk[Long], 
    title: String, content: String, postedAt: Date, author_id: Long
)
 
object Post extends Magic[Post]{
  def allWithAuthor:List[(Post,User)] = 
    SQL(
        """
            select * from Post p 
            join User u on p.author_id = u.id 
            order by p.postedAt desc
        """
    ).as( Post ~< User ^^ flatten * )

  def allWithAuthorAndComments:List[(Post,User,List[Comment])] = 
    SQL(
        """
            select * from Post p 
            join User u on p.author_id = u.id 
            left join Comment c on c.post_id = p.id 
            order by p.postedAt desc
        """
    ).as( Post ~< User ~< Post.spanM( Comment ) ^^ flatten * )

  def byIdWithAuthorAndComments(id: Long):Option[(Post,User,List[Comment])] = 
    SQL(
        """
            select * from Post p 
            join User u on p.author_id = u.id 
            left join Comment c on c.post_id = p.id 
            where p.id = {id}
        """
    ).on("id" -> id).as( Post ~< User ~< Post.spanM( Comment ) ^^ flatten ? )
}

case class Comment(
    id: Pk[Long], 
    author: String, content: String, postedAt: Date, post_id: Long
) 
 
object Comment extends Magic[Comment]{
    def apply(post_id: Long, author: String, content: String) = {
        new Comment(NotAssigned, author, content, new Date(), post_id)
    }
}
