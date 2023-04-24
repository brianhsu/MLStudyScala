package regression.model

sealed trait Gender

object Gender {
  case object Male extends Gender
  case object Female extends Gender

  def apply(string: String): Gender = string match {
    case "Male" => Male
    case "Female" => Female
  }
}

