package views.html
package coach

import lila.app.templating.Environment.{ given, * }
import lila.app.ui.ScalatagsTemplate.{ *, given }
import lila.i18n.LangList

import controllers.routes

object widget:

  import trans.coach.*

  def titleName(c: lila.coach.Coach.WithUser) =
    frag(
      c.user.title.map: t =>
        s"$t ",
      c.user.realNameOrUsername
    )

  def apply(c: lila.coach.Coach.WithUser, link: Boolean)(using Context) =
    val profile = c.user.profileOrDefault
    frag(
      link.option(a(cls := "overlay", href := routes.Coach.show(c.user.username))),
      picture.thumbnail(c, if link then 300 else 350),
      div(cls := "overview")(
        (if link then h2 else h1) (cls := "coach-name")(titleName(c)),
        c.coach.profile.headline
          .map: h =>
            p(
              cls := s"headline ${
                  if h.length < 60 then "small" else if h.length < 120 then "medium" else "large"
                }"
            )(h),
        table(
          tbody(
            tr(
              th(location()),
              td(
                profile.nonEmptyLocation.map: l =>
                  span(cls := "location")(l),
                profile.flagInfo.map: c =>
                  frag(
                    span(cls := "flag")(
                      img(src := assetUrl(s"images/flags/${c.code}.png")),
                      " ",
                      c.name
                    )
                  )
              )
            ),
            tr(cls := "languages")(
              th(languages()),
              td(c.coach.languages.map(LangList.name).mkString(", "))
            ),
            tr(cls := "rating")(
              th(rating()),
              td(
                profile.fideRating.map { r =>
                  frag("FIDE: ", r)
                },
                a(href := routes.User.show(c.user.username))(
                  c.user.perfs.best6Perfs
                    .filter(c.user.perfs.hasEstablishedRating)
                    .map:
                      showPerfRating(c.user.perfs, _)
                )
              )
            ),
            c.coach.profile.hourlyRate.map: r =>
              tr(cls := "rate")(
                th(hourlyRate()),
                td(r)
              ),
            (!link).option(
              tr(cls := "available")(
                th(availability()),
                td:
                  if c.coach.available.yes
                  then span(cls := "text", dataIcon := licon.Checkmark)(accepting())
                  else span(cls := "text", dataIcon := licon.X)(notAccepting())
              )
            ),
            c.user.seenAt.map: seen =>
              tr(cls := "seen")(
                th,
                td(trans.lastSeenActive(momentFromNow(seen)))
              )
          )
        )
      )
    )
