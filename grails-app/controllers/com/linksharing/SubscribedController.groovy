package com.linksharing

import org.springframework.security.access.annotation.Secured

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

@Secured(['ROLE_ADMIN', 'ROLE_USER'])
@Transactional(readOnly = true)
class SubscribedController {

    SubscribedImp subscribedImp
    def springSecurityService

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        List subscribed = Subscribed.findAllByUser(session.getAttribute('user'), params)
        respond subscribed, model: [subscribedInstanceCount: subscribed.size()]

    }

    def show(Subscribed subscribedInstance) {
        respond subscribedInstance
    }

    def create() {
        Topic topicInstance
        User user = springSecurityService.getCurrentUser()
        int topicId = Integer.parseInt(params.topicId)

        if(topicId)
            topicInstance= Topic.findById(topicId)

        if (user && topicInstance)
            respond new Subscribed(params), model: [user: user, topicInstance: topicInstance]
        else
            render 'some error occur'
    }

    @Transactional
    def save(Subscribed subscribedInstance) {
        println 'from top of Subscribed save'
        if (subscribedInstance == null) {
            notFound()
            return
        }

        if (subscribedInstance.hasErrors()) {
            respond subscribedInstance.errors, view: 'create', [status: CREATED]
            return
        }

        boolean flag =  isUserSubscribed(subscribedInstance);
        println flag
        if (flag) {
            redirect(controller: 'subscribed', action: 'create', params: [errMsg: "You Can't Subscribe a Topic Again", topicId: subscribedInstance.topic.id])
            return

        } else
            subscribedInstance.save flush: true


        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'subscribed.label', default: 'Subscribed'), subscribedInstance.id])
                redirect subscribedInstance
            }
            '*' { respond subscribedInstance, [status: CREATED] }
        }
    }

    def edit(Subscribed subscribedInstance) {
        respond subscribedInstance
    }

    @Transactional
    def update(Subscribed subscribedInstance) {
        if (subscribedInstance == null) {
            notFound()
            return
        }

        if (subscribedInstance.hasErrors()) {
            respond subscribedInstance.errors, view: 'edit'
            return
        }

        subscribedInstance.save flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Subscribed.label', default: 'Subscribed'), subscribedInstance.id])
                redirect subscribedInstance
            }
            '*' { respond subscribedInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Subscribed subscribedInstance) {

        if (subscribedInstance == null) {
            notFound()
            return
        }

        subscribedInstance.delete flush: true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Subscribed.label', default: 'Subscribed'), subscribedInstance.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'subscribed.label', default: 'Subscribed'), params.id])
                redirect action: "index", method: "GET"
            }
            '*' { render status: NOT_FOUND }
        }
    }

    public  boolean isUserSubscribed(Subscribed subscribed){
        return Subscribed.countByUserAndTopic(subscribed.user,subscribed.topic)>0

    }
}
