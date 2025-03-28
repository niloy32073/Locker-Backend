package com.dbytes.routes

import com.dbytes.helpers.withAdminRole
import com.dbytes.models.Building
import com.dbytes.services.BuildingServices
import com.dbytes.services.UserServices
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.buildingRoutes(buildingServices: BuildingServices,userServices: UserServices) {
    routing {
        authenticate("auth-jwt") {
            get("/buildings") {
                try {
                    val buildings = buildingServices.getAllBuildings()
                    call.respond(HttpStatusCode.OK,buildings)
                }
                catch(ex:Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unknown error")
                }
            }
            post("/addBuilding"){
                call.withAdminRole(userServices){
                    try {
                        val building = call.receive<Building>()
                        val buildingId = buildingServices.createBuilding(building)
                        call.respond(HttpStatusCode.Created,buildingId)
                    }
                    catch(ex:Exception) {
                        call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unknown error")
                    }
                }
            }
            post("/updateBuilding"){
                call.withAdminRole(userServices){
                    try {
                        val building = call.receive<Building>()
                        buildingServices.updateBuilding(building = building)
                        call.respond(HttpStatusCode.OK,"Update successful")
                    }
                    catch(ex:Exception) {
                        call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unknown error")
                    }
                }
            }
            get("/buildings/{buildingId}"){
                try {
                    val buildingId = call.parameters["buildingId"].toString().toLong()
                    val building = buildingServices.findBuildingById(buildingId)
                    if(building == null){
                        call.respond(HttpStatusCode.NotFound)
                    }
                    else
                        call.respond(HttpStatusCode.OK,building)

                }catch(ex:Exception) {
                    call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unknown error")
                }
            }
            delete("/buildings/{buildingId}"){
                call.withAdminRole(userServices){
                    try {
                        val buildingId = call.parameters["buildingId"].toString().toLong()
                        buildingServices.deleteBuilding(buildingId)
                        call.respond(HttpStatusCode.OK,"Building deleted successfully")
                    }catch(ex:Exception) {
                        println(ex.message)
                        call.respond(HttpStatusCode.InternalServerError, ex.message ?: "Unknown error")
                    }
                }
            }
        }
    }
}