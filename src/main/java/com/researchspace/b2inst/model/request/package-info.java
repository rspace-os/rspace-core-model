/**
 * Request bodies sent by the {@code b2inst_submit.bash} submission flow.
 *
 * <ul>
 *   <li>{@link com.researchspace.b2inst.model.request.B2instDoi} - STEP 1,
 *       create the draft record.</li>
 *   <li>{@link com.researchspace.b2inst.model.request.FileEntryKey} - STEP 2,
 *       declare file entries (sent as a JSON array).</li>
 *   <li>{@link com.researchspace.b2inst.model.request.ReviewRequest} - STEP 5,
 *       create the community-submission review request.</li>
 * </ul>
 *
 * <p>STEP 3 uploads raw bytes (no JSON body) and STEPS 4 and 6 send no body, so
 * they have no request type here.</p>
 */
package com.researchspace.b2inst.model.request;
