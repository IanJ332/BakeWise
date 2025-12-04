package com.example.bakewise

object ScheduleRepository {
    val savedSchedules = mutableListOf<SavedSchedule>()

    fun cleanupExpiredSchedules() {
        val now = System.currentTimeMillis()
        val iterator = savedSchedules.iterator()
        
        while (iterator.hasNext()) {
            val schedule = iterator.next()
            // Find the time of the last step
            val lastStepTime = schedule.scheduleItems.maxOfOrNull { it.whenMillis } ?: 0L
            // Assume baking takes roughly 2 hours after the last step starts
            val expirationTime = lastStepTime + (2 * 60 * 60 * 1000) 
            
            // Check if expired AND not currently being actively baked in the session
            // (Assuming CurrentBakeSession logic is handled or we just clean up regardless of session state if time is up)
            if (now > expirationTime) {
                // Auto-archive to Past Loaves
                // We need to avoid duplicate archives if possible, but for this simple list, moving it once is fine.
                val loaf = PastLoaf(
                    recipeName = schedule.recipeName,
                    dateBaked = java.util.Date(lastStepTime), 
                    notes = emptyList() 
                )
                PastLoavesRepository.addLoaf(loaf)
                
                // Remove from current schedules
                iterator.remove()
            }
        }
    }
}
