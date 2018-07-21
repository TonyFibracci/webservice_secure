package com.nashtools.bot.framework;

public class Entries<T> {

	long num_entries_per_bucket;
	long total_num_entries;
	T[] entries;
	boolean data_was_loaded;

	public Entries(long num_entries_per_bucket, long total_num_entries) {
		this.num_entries_per_bucket = num_entries_per_bucket;
		this.total_num_entries = total_num_entries;
	}

	public Entries(long num_entries_per_bucket2, long total_num_entries2,
			T[] data) {
		this.num_entries_per_bucket = num_entries_per_bucket2;
		this.total_num_entries = total_num_entries2;
		entries = data;
	}

	long get_entry_index(int bucket, long soln_idx) {
		return (num_entries_per_bucket * bucket) + soln_idx;
	}

	Entries<T> new_loaded_entries(long num_entries_per_bucket,
			long total_num_entries, Object[][] data) {
		/* First, read the entry type */
		Object type = data[0][0];
		int pointer = 0;

		/*
		 * Load the appropriate type of entries and advance data past the
		 * entries
		 */
		Entries entries = null;
		if (type instanceof Integer) {
			Integer[] int_data = (Integer[]) data[0];
			entries = new Entries<Integer>(num_entries_per_bucket,
					total_num_entries, int_data);
			pointer++;
		}

		else if (type instanceof Long) {
			Long[] int_data = (Long[]) data[0];
			entries = new Entries<Long>(num_entries_per_bucket,
					total_num_entries, int_data);
			pointer++;
		}

		else {
			System.out.println("unrecognized entry type [%d]\n");
		}

		return entries;
	}

	long get_pos_values(int bucket, long soln_idx, int num_choices,
			long[] values) {
		/* Get the local entries at this index */
		long base_index = get_entry_index(bucket, soln_idx);
		long[] local_entries = new long[num_choices];
		System.arraycopy(entries, (int) base_index, local_entries, 0,
				num_choices * local_entries.length);

		/* Zero out negative values and store in the returned array */
		long sum_values = 0;
		for (int c = 0; c < num_choices; ++c) {
			if (local_entries[c] > 0)
				local_entries[c] *= local_entries[c];
			values[c] = local_entries[c];
			sum_values += local_entries[c];
		}

		return sum_values;
	}
	
	long get_pos_values(int bucket, long soln_idx, BettingNode node,
			long[] values) {
		InfoSetNode n = (InfoSetNode)node;
		/* Get the local entries at this index */
		long base_index = get_entry_index(bucket, soln_idx);
		long[] local_entries = new long[n.num_choices];
		System.arraycopy(entries, (int) base_index, local_entries, 0,
				n.num_choices * local_entries.length);

		/* Zero out negative values and store in the returned array */
		long sum_values = 0;
		for (int c = 0; c < n.num_choices; ++c) {
			if (local_entries[c] > 0)
				local_entries[c] *= local_entries[c];
			values[c] = local_entries[c];
			sum_values += local_entries[c];
		}

		return sum_values;
	}

}
