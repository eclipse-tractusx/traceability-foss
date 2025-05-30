package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.model.response;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IrsBatchResponseTest {

    @Test
    void testConstructorAndAccessors() {
        Date now = new Date();
        IrsBatchResponse.JobRecord job = new IrsBatchResponse.JobRecord(now, "job123", now, "COMPLETED");
        List<IrsBatchResponse.JobRecord> jobs = List.of(job);

        IrsBatchResponse response = new IrsBatchResponse(
                "batch001",
                1,
                "FINISHED",
                10,
                now,
                jobs,
                1,
                "orderABC",
                now,
                10
        );

        assertEquals("batch001", response.batchId());
        assertEquals(1, response.batchNumber());
        assertEquals("FINISHED", response.batchProcessingState());
        assertEquals(10, response.batchTotal());
        assertEquals(now, response.completedOn());
        assertEquals(jobs, response.jobs());
        assertEquals(1, response.jobsInBatchChecksum());
        assertEquals("orderABC", response.orderId());
        assertEquals(now, response.startedOn());
        assertEquals(10, response.totalJobs());
    }

    @Test
    void testJobRecordAccessors() {
        Date now = new Date();
        IrsBatchResponse.JobRecord job = new IrsBatchResponse.JobRecord(now, "job999", now, "PENDING");

        assertEquals("job999", job.id());
        assertEquals("PENDING", job.state());
        assertEquals(now, job.startedOn());
        assertEquals(now, job.completedOn());
    }

    @Test
    void testWithNullValues() {
        IrsBatchResponse.JobRecord job = new IrsBatchResponse.JobRecord(null, null, null, null);
        IrsBatchResponse response = new IrsBatchResponse(
                null,
                0,
                null,
                0,
                null,
                null,
                0,
                null,
                null,
                0
        );

        assertNull(job.completedOn());
        assertNull(job.id());
        assertNull(job.startedOn());
        assertNull(job.state());

        assertNull(response.batchId());
        assertEquals(0, response.batchNumber());
        assertNull(response.batchProcessingState());
        assertEquals(0, response.batchTotal());
        assertNull(response.completedOn());
        assertNull(response.jobs());
        assertEquals(0, response.jobsInBatchChecksum());
        assertNull(response.orderId());
        assertNull(response.startedOn());
        assertEquals(0, response.totalJobs());
    }

    @Test
    void testWithEmptyJobsList() {
        Date now = new Date();
        List<IrsBatchResponse.JobRecord> emptyJobs = Collections.emptyList();

        IrsBatchResponse response = new IrsBatchResponse(
                "batch002",
                2,
                "RUNNING",
                5,
                now,
                emptyJobs,
                0,
                "orderXYZ",
                now,
                5
        );

        assertNotNull(response.jobs());
        assertTrue(response.jobs().isEmpty());
    }
}
