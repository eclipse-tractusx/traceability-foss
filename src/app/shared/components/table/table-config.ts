export class TableConfig {
  constructor(private isTableEditable = false, private emptyState?: { emptyStateReason?: string }) {}

  get isReadOnly(): boolean {
    return !this.isTableEditable;
  }

  get emptyStateReason(): string | undefined {
    if (!this.emptyState) {
      return undefined;
    }
    return this.emptyState.emptyStateReason;
  }

  get options(): { emptyStateReason?: string } {
    if (this.emptyState) {
      return this.emptyState;
    }
    return {};
  }

  set options(value: { emptyStateReason?: string }) {
    this.emptyState = value;
  }
}
