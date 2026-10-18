package com.example.data.firebase

import android.content.Context
import com.example.R
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreTheSkullsRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    // Primary constructor resolving database ID from XML config
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        ),
        Firebase.auth
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("يجب تسجيل الدخول بواسطة حساب Google للوصول إلى قاعدة البيانات.")
    }

    // Observe User Profile
    fun observeUserProfile(userId: String): Flow<UserProfile?> {
        val path = "users/$userId"
        return db.collection("users").document(userId)
            .snapshots()
            .map { it.toObject(UserProfile::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.GET, path)
                emit(null)
            }
    }

    // Create or update User Profile
    suspend fun saveUserProfile(profile: UserProfile) {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        try {
            val payload = mapOf(
                "userId" to uid,
                "displayName" to profile.displayName.ifBlank { "لاعب سكلز" },
                "email" to profile.email,
                "photoUrl" to profile.photoUrl,
                "gamerTag" to profile.gamerTag.ifBlank { "SkullGamer" },
                "favoritePlatform" to profile.favoritePlatform,
                "createdAt" to (profile.createdAt ?: FieldValue.serverTimestamp()),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // Observe Likes for a review
    fun observeReviewLikes(reviewId: String): Flow<List<ReviewLike>> {
        val path = "reviews/$reviewId/likes"
        return db.collection("reviews").document(reviewId).collection("likes")
            .snapshots()
            .map { it.toObjects(ReviewLike::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    // Toggle Like
    suspend fun toggleLike(reviewId: String): Boolean {
        val uid = requireUserId()
        val docRef = db.collection("reviews").document(reviewId).collection("likes").document(uid)
        return try {
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                docRef.delete().await()
                false // now unliked
            } else {
                val payload = mapOf(
                    "userId" to uid,
                    "reviewId" to reviewId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
                true // now liked
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            throw e
        }
    }

    // Observe Comments for a review
    fun observeReviewComments(reviewId: String): Flow<List<ReviewComment>> {
        val path = "reviews/$reviewId/comments"
        return db.collection("reviews").document(reviewId).collection("comments")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .snapshots()
            .map { it.toObjects(ReviewComment::class.java) }
            .catch { e ->
                if (e is Exception) handleFirestoreError(e, OperationType.LIST, path)
                emit(emptyList())
            }
    }

    // Add Comment to a review
    suspend fun addComment(
        reviewId: String,
        commentText: String,
        rating: Double,
        gamerTag: String
    ) {
        val uid = requireUserId()
        val user = auth.currentUser
        val collectionRef = db.collection("reviews").document(reviewId).collection("comments")
        val docRef = collectionRef.document()

        try {
            val payload = mapOf(
                "id" to docRef.id,
                "reviewId" to reviewId,
                "userId" to uid,
                "userName" to (user?.displayName ?: "لاعب سكلز"),
                "userPhoto" to (user?.photoUrl?.toString() ?: ""),
                "gamerTag" to gamerTag.ifBlank { "SkullPlayer" },
                "comment" to commentText,
                "rating" to rating.coerceIn(1.0, 10.0),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, docRef.path)
            throw e
        }
    }

    // Delete Comment
    suspend fun deleteComment(reviewId: String, commentId: String) {
        val uid = requireUserId()
        val docRef = db.collection("reviews").document(reviewId).collection("comments").document(commentId)
        try {
            docRef.delete().await()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.DELETE, docRef.path)
            throw e
        }
    }
}
