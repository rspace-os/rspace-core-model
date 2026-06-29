/**
 * Response bodies returned by the {@code b2inst_submit.bash} submission flow.
 *
 * <ul>
 *   <li>{@link com.researchspace.b2inst.model.response.B2instDraftRecord} - STEP 1,
 *       the created draft (source of the record id / RID).</li>
 *   <li>{@link com.researchspace.b2inst.model.response.B2instDraftFileList} - STEP 2,
 *       declared file entries; {@link com.researchspace.b2inst.model.response.B2instDraftFile}
 *       is the per-file entry also returned by STEPS 3 and 4.</li>
 *   <li>{@link com.researchspace.b2inst.model.response.B2instRequestResponse} - STEPS 5
 *       and 6, the community-submission request (source of the submit link).</li>
 * </ul>
 */
package com.researchspace.b2inst.model.response;
