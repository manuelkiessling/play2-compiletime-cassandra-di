package controllers

import models.ProductModel
import play.api._
import play.api.mvc._
import repositories.Repository

class Application(productsRepository: Repository[ProductModel, Int]) extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

}
