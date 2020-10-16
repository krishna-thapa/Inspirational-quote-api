package auth.model

import java.sql.Date

import play.api.libs.json.{ Json, OFormat }

case class UserList(
    name: String,
    email: String,
    joinedDate: Date,
    isAdmin: Boolean
)

object UserList {
  implicit lazy val userListFormat: OFormat[UserList] = Json.format[UserList]

  def apply(userInfo: UserInfo): UserList =
    new UserList(
      s"${userInfo.firstName} ${userInfo.lastName}",
      userInfo.email,
      userInfo.createdDate,
      userInfo.isAdmin
    )
}
