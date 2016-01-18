package controllers

import models.ProductModel
import play.api._
import play.api.mvc._
import repositories.Repository

class Application(productsRepository: Repository[ProductModel, Int]) extends Controller {

  def index = Action {
    val product = productsRepository.getOneById(1)
    Ok(views.html.index(s"Your new application is ready. The name of product #1 is ${product.name}."))
  }

}
