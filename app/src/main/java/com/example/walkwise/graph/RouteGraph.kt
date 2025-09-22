package com.example.walkwise.graph

import android.content.Context
import com.example.walkwise.modeltraining.ModelTrainer

data class Place(val name: String, val latitude: Double, val longitude: Double)

data class Edge(val source: Place, val destination: Place, var cost: Double, val walking_distance: Double,
                val bus_transportation_distance: Double)

class RouteGraph {
    val adjacencyMap = mutableMapOf<Place, MutableList<Edge>>()

    init {
        val intrare1FacultateaDeAutomatica = Place("Intrare 1 Facultatea de Automatica si Calculatoare", 44.43451048793155, 26.0485076768541)
        val parcare1FacultateaDeAutomatica = Place("Parcare 1 Facultatea de Automatica si Calculatoare", 44.434696511254096, 26.048500075633633)
        val parcare2FacultateaDeAutomatica = Place("Parcare 2 Facultatea de Automatica si Calculatoare", 44.43474339676192, 26.048124148482703)
        val parcare3FacultateaDeAutomatica = Place("Parcare 3 Facultatea de Automatica si Calculatoare", 44.435363247629766, 26.048116647891668)
        val trecerePietoniFacultateaDeAutomatica = Place("Trecere Pietoni Facultatea de Automatica si Calculatoare", 44.435374984647176, 26.04776768416797)
        val facultateaDeAutomaticaSiCalculatoare = Place("Facultatea de Automatica si Calculatoare", 44.435733042604014, 26.04770268077216)
        val facultateaDeEnergetica = Place("Facultatea de Energetica", 44.43734155703561, 26.049120391514446)
        val rectoratUNSTPB = Place("Rectorat UNSTPB", 44.438077816922124, 26.051511207798576)
        val precisIntrare1 = Place("Precis Intrare 1", 44.43515881523721, 26.0477365081896)
        val intrare2FacultateaDeAutomatica = Place("Intrare 2 Facultatea de Automatica si Calculatoare", 44.43446017177663, 26.045134751908222)
        val stradaUniversitatii1 = Place("Strada Universitatii 1", 44.435344546662364, 26.046166373673014)
        val institutulCampus = Place("Institutul Campus", 44.43495895500657, 26.046177559989147)
        val stradaUniversitatii2 = Place("Strada Universitatii 2", 44.43533016269567, 26.045321878144843)
        val facultateaDeInginerieElectrica = Place("Facultatea de Inginerie Electrica", 44.436135414372636, 26.04556723109547)
        val faima = Place("Facultatea De Antreprenoriat, Inginerie si Managementul Afacerilor", 44.44027148358401, 26.05018909824151)
        val punctIntersectie1 = Place("Punct Intersectie 1", 44.44050239003634, 26.05027567166923)
        val aula = Place("UNSTPB Aula Magna", 44.44034258917764, 26.051216103037234)
        val punctIntersectie2 = Place("Punct Intersectie 2", 44.44077263324292, 26.0503740161745)
        val fiir = Place("Facultatea de Inginerie Industriala si Robotica", 44.440892648214195, 26.049553848137737)
        val punctIntersectie3 = Place("Punct Intersectie 3", 44.44103293154233, 26.05048514887986)
        val biblioteca = Place("UNSTPB Biblioteca Centrala", 44.440876234558566, 26.051338538883694)
        val punctIntersectie4 = Place("Punct Intersectie 4", 44.43801200045292, 26.051826410986436)
        val facultateaDeTransport = Place("Facultatea de Transport", 44.43964485763968, 26.052340767405667)
        val fils = Place("Facultatea de Inginerie In Limbi Straine", 44.439795345856865, 26.05239834303463)
        val fsim = Place("Facultatea de Stiinta si Ingineria Materialelor", 44.439938026842114, 26.052326139871223)
        val punctIntersectie5 = Place("Punct Intersectie 5", 44.440117998619684, 26.05239185162604)
        val punctIntersectie6 = Place("Punct Intersectie 6", 44.4406601101007, 26.052490719320154)
        val accesSplaiulIndependentei = Place("Acces Splaiul Independentei", 44.444152027900884, 26.053751797740265)
        val idmClub = Place("IDM Club", 44.44495276120948, 26.04952314609253)
        val metrouPetrachePoenaru = Place("Statie Metrou Petrache Poenaru", 44.4454467207967, 26.04673680234155)
        val punctIntersectie7 = Place("Punct Intersectie 7", 44.4374832274102, 26.045523008372676)
        val punctIntersectie8 = Place("Punct Intersectie 8", 44.43746590118453, 26.04666186400934)
        val punctIntersectie9 = Place("Punct Intersectie 9", 44.43798004661127, 26.046942347645473)
        val facultateaDeEnergetica2 = Place("Facultatea de Energetica 2", 44.43768245465861, 26.048097015835104)
        val punctIntersectie10 = Place("Punct Intersectie 10", 44.43929141341456, 26.047402909799338)
        val fisb = Place("Facultatea de Ingineria Sistemelor Biotehnice", 44.43950944864978, 26.046266350932484)
        val precisIntrare2 = Place("Precis Intrare 2", 44.43473672971849, 26.047701512286256)
        val punctIntersectie11 = Place("Punct Intersectie 11", 44.43873469872327, 26.049629172342307)
        val lidl = Place("Lidl", 44.43444641356842, 26.043726778032248)
        val statieMetrouGrozavesti = Place("Statie Metrou Grozavesti", 44.44290610886431, 26.06007408056468)
        val statieMetrouPolitehnica = Place("Statie Metrou Politehnica", 44.43460284119391, 26.054507493643275)
        val punctIntersectie12 = Place("Punct Intersectie 12", 44.43442386008412, 26.054519096174673)
        val punctIntersectie13 = Place("Punct Intersectie 13", 44.43441588118544, 26.054217901462806)
        val caminLeuA = Place("Camin Leu A", 44.434434971699595, 26.055248632842908)
        val afi = Place("Afi Cotroceni", 44.428974855690065, 26.054285408326695)
        val punctIntersectie14 = Place("Punct Intersectie 14", 44.434622219133466, 26.054184400108692)

        addNode(intrare1FacultateaDeAutomatica)
        addNode(parcare1FacultateaDeAutomatica)
        addNode(parcare2FacultateaDeAutomatica)
        addNode(parcare3FacultateaDeAutomatica)
        addNode(trecerePietoniFacultateaDeAutomatica)
        addNode(facultateaDeAutomaticaSiCalculatoare)
        addNode(facultateaDeEnergetica)
        addNode(rectoratUNSTPB)
        addNode(precisIntrare1)
        addNode(intrare2FacultateaDeAutomatica)
        addNode(stradaUniversitatii1)
        addNode(institutulCampus)
        addNode(stradaUniversitatii2)
        addNode(facultateaDeInginerieElectrica)
        addNode(faima)
        addNode(punctIntersectie1)
        addNode(aula)
        addNode(punctIntersectie2)
        addNode(fiir)
        addNode(punctIntersectie3)
        addNode(biblioteca)
        addNode(punctIntersectie4)
        addNode(facultateaDeTransport)
        addNode(fils)
        addNode(fsim)
        addNode(punctIntersectie5)
        addNode(punctIntersectie6)
        addNode(accesSplaiulIndependentei)
        addNode(idmClub)
        addNode(metrouPetrachePoenaru)
        addNode(punctIntersectie7)
        addNode(punctIntersectie8)
        addNode(punctIntersectie9)
        addNode(facultateaDeEnergetica2)
        addNode(punctIntersectie10)
        addNode(fisb)
        addNode(precisIntrare2)
        addNode(punctIntersectie11)
        addNode(lidl)
        addNode(statieMetrouGrozavesti)
        addNode(statieMetrouPolitehnica)
        addNode(punctIntersectie12)
        addNode(punctIntersectie13)
        addNode(caminLeuA)
        addNode(afi)
        addNode(punctIntersectie14)

        addEdge(intrare1FacultateaDeAutomatica, parcare1FacultateaDeAutomatica, 0.0, 23.0, 0.0)
        addEdge(parcare1FacultateaDeAutomatica, parcare2FacultateaDeAutomatica, 0.0, 32.0, 0.0)
        addEdge(parcare2FacultateaDeAutomatica, parcare3FacultateaDeAutomatica, 0.0, 71.0, 0.0)
        addEdge(parcare3FacultateaDeAutomatica, trecerePietoniFacultateaDeAutomatica, 0.0, 27.0, 0.0)
        addEdge(trecerePietoniFacultateaDeAutomatica, facultateaDeAutomaticaSiCalculatoare, 0.0, 34.0, 0.0)
        addEdge(parcare3FacultateaDeAutomatica, facultateaDeEnergetica, 0.0, 250.0, 0.0)
        addEdge(facultateaDeEnergetica, rectoratUNSTPB, 0.0, 260.0, 0.0)
        addEdge(trecerePietoniFacultateaDeAutomatica, precisIntrare1, 0.0, 27.0, 0.0)
        addEdge(intrare1FacultateaDeAutomatica, intrare2FacultateaDeAutomatica, 0.0, 270.0, 0.0)
        addEdge(trecerePietoniFacultateaDeAutomatica, stradaUniversitatii1, 0.0, 130.0, 0.0)
        addEdge(stradaUniversitatii1, institutulCampus, 0.0, 43.0, 0.0)
        addEdge(stradaUniversitatii1, stradaUniversitatii2, 0.0, 67.0, 0.0)
        addEdge(intrare2FacultateaDeAutomatica, stradaUniversitatii2, 0.0, 110.0, 0.0)
        addEdge(stradaUniversitatii2, facultateaDeInginerieElectrica, 0.0, 100.0, 0.0)
        addEdge(faima, punctIntersectie1, 0.0, 27.0, 0.0)
        addEdge(punctIntersectie1, aula, 0.0, 76.0, 0.0)
        addEdge(punctIntersectie1, punctIntersectie2, 0.0, 31.0, 0.0)
        addEdge(punctIntersectie2, fiir, 0.0, 76.0, 0.0)
        addEdge(punctIntersectie2, punctIntersectie3, 0.0, 30.0, 0.0)
        addEdge(punctIntersectie3, biblioteca, 0.0, 70.0, 0.0)
        addEdge(rectoratUNSTPB, punctIntersectie4, 0.0, 27.0, 0.0)
        addEdge(punctIntersectie4, facultateaDeTransport, 0.0, 190.0, 0.0)
        addEdge(facultateaDeTransport, fils, 0.0, 17.0, 0.0)
        addEdge(fils, fsim, 0.0, 23.0, 0.0)
        addEdge(aula, punctIntersectie5, 0.0, 97.0, 0.0)
        addEdge(punctIntersectie5, fsim, 0.0, 21.0, 0.0)
        addEdge(biblioteca, punctIntersectie6, 0.0, 95.0, 0.0)
        addEdge(punctIntersectie6, punctIntersectie5, 0.0, 62.0, 0.0)
        addEdge(punctIntersectie6, accesSplaiulIndependentei, 0.0, 400.0, 0.0)
        addEdge(accesSplaiulIndependentei, idmClub, 0.0, 350.0, 0.0)
        addEdge(idmClub, metrouPetrachePoenaru, 0.0, 230.0, 0.0)
        addEdge(facultateaDeInginerieElectrica, punctIntersectie7, 0.0, 160.0, 0.0)
        addEdge(punctIntersectie7, punctIntersectie8, 0.0, 94.0, 0.0)
        addEdge(punctIntersectie8, punctIntersectie9, 0.0, 62.0, 0.0)
        addEdge(punctIntersectie9, facultateaDeEnergetica2, 0.0, 100.0, 0.0)
        addEdge(punctIntersectie9, punctIntersectie10, 0.0, 150.0, 0.0)
        addEdge(punctIntersectie10, fisb, 0.0, 93.0, 0.0)
        addEdge(parcare2FacultateaDeAutomatica, precisIntrare2, 0.0, 32.0, 0.0)
        addEdge(facultateaDeEnergetica, punctIntersectie11, 0.0, 160.0, 0.0)
        addEdge(punctIntersectie11, faima, 0.0, 180.0, 0.0)
        addEdge(punctIntersectie11, punctIntersectie11, 0.0, 190.0, 0.0)
        addEdge(intrare2FacultateaDeAutomatica, lidl, 0.0, 110.0, 0.0)
        addEdge(accesSplaiulIndependentei, statieMetrouGrozavesti, 0.0, 500.0, 311.0)
        addEdge(statieMetrouPolitehnica, punctIntersectie12, 0.0, 18.0, 0.0)
        addEdge(punctIntersectie12, punctIntersectie13, 0.0, 21.0, 0.0)
        addEdge(punctIntersectie12, caminLeuA, 0.0, 60.0, 0.0)
        addEdge(punctIntersectie13, afi, 0.0, 600.0, 379.0)
        addEdge(statieMetrouPolitehnica, punctIntersectie14, 0.0, 24.0, 0.0)
        addEdge(punctIntersectie14, intrare1FacultateaDeAutomatica, 0.0, 450.0, 371.0)
        addEdge(punctIntersectie13, punctIntersectie14, 0.0, 25.0, 0.0)
    }

    fun addNode(place: Place) {
        adjacencyMap[place] = mutableListOf()
    }

    fun addEdge(source: Place, destination: Place, cost: Double, walking_distance: Double,
                bus_transportation_distance: Double) {
        adjacencyMap[source]?.add(Edge(source, destination, cost, walking_distance, bus_transportation_distance))
        adjacencyMap[destination]?.add(Edge(destination, source, cost, walking_distance, bus_transportation_distance)) // Assuming bidirectional edges
    }

    fun dijkstra(source: Place, destination: Place): List<Place> {
        val distances = mutableMapOf<Place, Double>()
        val previous = mutableMapOf<Place, Place?>() // Change to nullable Place
        val visited = mutableSetOf<Place>()

        adjacencyMap.keys.forEach { place ->
            distances[place] = Double.MAX_VALUE
            previous[place] = null
        }

        distances[source] = 0.0

        while (visited.size < adjacencyMap.size) {
            val current = getClosestVertex(distances, visited) ?: break

            visited.add(current)

            adjacencyMap[current]?.forEach { edge ->
                val alt = distances[current]!! + edge.cost
                if (alt < distances[edge.destination]!!) {
                    distances[edge.destination] = alt
                    previous[edge.destination] = current
                }
            }
        }

        val path = mutableListOf<Place>()
        var current: Place? = destination
        while (current != null) {
            path.add(current)
            current = previous[current]
        }
        return path.reversed()
    }

    fun updateCosts(context: Context) {
        adjacencyMap.values.forEach { edges ->
            edges.forEach { edge ->
                val source = edge.source
                val destination = edge.destination
                val walking_distance = edge.walking_distance
                val bus_travel_distance = edge.bus_transportation_distance

                val time = ModelTrainer.getInstance(context).infer(source.latitude, source.longitude,
                    destination.latitude, destination.longitude, walking_distance, bus_travel_distance)

                edge.cost = time.walkingTime.toDouble()
            }
        }
    }

    private fun getClosestVertex(distances: MutableMap<Place, Double>, visited: Set<Place>): Place? {
        var minDistance = Double.MAX_VALUE
        var closestVertex: Place? = null
        distances.forEach { (place, distance) ->
            if (distance < minDistance && place !in visited) {
                minDistance = distance
                closestVertex = place
            }
        }
        return closestVertex
    }

    companion object {
        val graph = RouteGraph()
    }
}
