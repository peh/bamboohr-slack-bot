package bamboohr.slack.bot.data

import bamboohr.slack.bot.model.User

import javax.inject.Singleton

@Singleton
class UserDatabase extends FileDatabase<User> {

    void save(User user) {
        save(user.id, user)
    }

    @Override
    User newEntity() {
        new User()
    }
}
